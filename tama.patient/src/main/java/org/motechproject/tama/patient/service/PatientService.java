package org.motechproject.tama.patient.service;

import ch.lambdaj.function.matcher.Predicate;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.reporting.MedicalHistoryRequestMapper;
import org.motechproject.tama.patient.reporting.PatientRequestMapper;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.motechproject.tama.patient.strategy.ChangedPatientPreferenceContext;
import org.motechproject.tama.patient.strategy.PatientPreferenceChangedStrategyFactory;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

@Component
public class PatientService {

    private PatientReportingService patientReportingService;
    private PatientRequestMapper requestMapper;
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimensCache allRegimens;
    private AllPatientEventLogs allPatientEventLogs;
    private PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory;
    private OutboxRegistry outboxRegistry;

    @Autowired
    public PatientService(PatientReportingService patientReportingService,
                          PatientRequestMapper requestMapper,
                          AllPatients allPatients,
                          AllTreatmentAdvices allTreatmentAdvices,
                          AllRegimensCache allRegimens,
                          AllPatientEventLogs allPatientEventLogs,
                          PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory,
                          OutboxRegistry outboxRegistry) {
        this.patientReportingService = patientReportingService;
        this.requestMapper = requestMapper;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
        this.allPatientEventLogs = allPatientEventLogs;
        this.preferenceChangedStrategyFactory = preferenceChangedStrategyFactory;
        this.outboxRegistry = outboxRegistry;
    }

    public void create(Patient patient, String clinicId, String userName) {
        allPatients.addToClinic(patient, clinicId, userName);
        outboxRegistry.getOutbox().enroll(patient);
        allPatientEventLogs.addAll(new ChangedPatientPreferenceContext(null, patient).getEventLogs());
        patientReportingService.save(requestMapper.map(patient), new MedicalHistoryRequestMapper().map(patient));
    }

    public void update(Patient patient, String userName) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        patient.setRegistrationDate(dbPatient.getRegistrationDate());
        patient.setActivationDate(dbPatient.getActivationDate());
        patient.setLastDeactivationDate(dbPatient.getLastDeactivationDate());
        patient.setLastSuspendedDate(dbPatient.getLastSuspendedDate());
        allPatients.update(patient, userName);
        patientReportingService.update(requestMapper.map(patient));

        final ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        if (changedPatientPreferenceContext.patientPreferenceHasChanged()) {
            preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext).execute(dbPatient, patient, allTreatmentAdvices.currentTreatmentAdvice(patient.getId()));
            allPatientEventLogs.addAll(changedPatientPreferenceContext.getEventLogs());
        }
    }

    public void activate(String id, String userName) {
        Patient patient = allPatients.get(id);
        patient.activate();
        allPatients.update(patient, userName);
        allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Activation));
    }

    public void deactivate(String id, Status deactivationStatus, String userName) {
        Patient patient = allPatients.get(id);
        patient.deactivate(deactivationStatus);
        allPatients.update(patient, userName);
        if (deactivationStatus.isTemporarilyDeactivated()) {
            allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Temporary_Deactivation));
        }
    }

    public void suspend(String patientId, String userName) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        allPatients.update(patient, userName);
        allPatientEventLogs.add(new PatientEventLog(patientId, PatientEvent.Suspension));
    }

    public Regimen currentRegimen(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        return treatmentAdvice == null ? null : allRegimens.getBy(treatmentAdvice.getRegimenId());
    }

    public PatientReport getPatientReport(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientDocId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
        return new PatientReport(patient, earliestTreatmentAdvice, currentTreatmentAdvice, currentRegimen(patient));
    }

    public List<PatientEventLog> getStatusChangeHistory(String patientDocId) {
        List<PatientEventLog> allPatientEventLogs = this.allPatientEventLogs.findByPatientId(patientDocId);
        return selectOnlyStatusChangeLogs(allPatientEventLogs);
    }

    private List<PatientEventLog> selectOnlyStatusChangeLogs(List<PatientEventLog> patientEventLogs) {
        Predicate<PatientEventLog> onlyStatusChangeLogs = new Predicate<PatientEventLog>() {
            @Override
            public boolean apply(PatientEventLog log) {
                List<PatientEvent> statusChangeEvents = Arrays.asList(PatientEvent.Activation, PatientEvent.Suspension, PatientEvent.Temporary_Deactivation);
                return statusChangeEvents.contains(log.getEvent());
            }
        };

        return filter(onlyStatusChangeLogs, patientEventLogs);
    }
}