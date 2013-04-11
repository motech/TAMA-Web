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
    private MedicalHistoryRequestMapper medicalHistoryRequestMapper;

    @Autowired
    public PatientService(PatientReportingService patientReportingService,
                          PatientRequestMapper requestMapper,
                          AllPatients allPatients,
                          AllTreatmentAdvices allTreatmentAdvices,
                          AllRegimensCache allRegimens,
                          AllPatientEventLogs allPatientEventLogs,
                          PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory,
                          OutboxRegistry outboxRegistry, MedicalHistoryRequestMapper medicalHistoryRequestMapper) {
        this.patientReportingService = patientReportingService;
        this.requestMapper = requestMapper;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
        this.allPatientEventLogs = allPatientEventLogs;
        this.preferenceChangedStrategyFactory = preferenceChangedStrategyFactory;
        this.outboxRegistry = outboxRegistry;
        this.medicalHistoryRequestMapper = medicalHistoryRequestMapper;
    }

    public void create(Patient patient, String clinicId, String userName) {
        allPatients.addToClinic(patient, clinicId, userName);
        outboxRegistry.getOutbox().enroll(patient);
        allPatientEventLogs.addAll(new ChangedPatientPreferenceContext(null, patient).getEventLogs(), userName);
        patientReportingService.save(requestMapper.map(patient), medicalHistoryRequestMapper.map(patient));
    }

    public void update(Patient patient, String userName) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        patient.setRegistrationDate(dbPatient.getRegistrationDate());
        patient.setActivationDate(dbPatient.getActivationDate());
        patient.setLastDeactivationDate(dbPatient.getLastDeactivationDate());
        patient.setLastSuspendedDate(dbPatient.getLastSuspendedDate());
        allPatients.update(patient, userName);
        patientReportingService.update(requestMapper.map(patient), medicalHistoryRequestMapper.map(patient));

        final ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        if (changedPatientPreferenceContext.patientPreferenceHasChanged()) {
            preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext).execute(dbPatient, patient, allTreatmentAdvices.currentTreatmentAdvice(patient.getId()));
            allPatientEventLogs.addAll(changedPatientPreferenceContext.getEventLogs(), userName);
        }
    }

    public void activate(String id, String userName) {
        Patient patient = allPatients.get(id);
        patient.activate();
        allPatients.update(patient, userName);
        patientReportingService.update(requestMapper.map(patient), medicalHistoryRequestMapper.map(patient));

        allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Activation), userName);
    }

    public void deactivate(String id, Status deactivationStatus, String userName) {
        Patient patient = allPatients.get(id);
        patient.deactivate(deactivationStatus);
        allPatients.update(patient, userName);
        patientReportingService.update(requestMapper.map(patient), medicalHistoryRequestMapper.map(patient));

        if (deactivationStatus.isTemporarilyDeactivated()) {
            allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Temporary_Deactivation), userName);
        }
    }

    public void suspend(String patientId, String userName) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        allPatients.update(patient, userName);
        patientReportingService.update(requestMapper.map(patient), medicalHistoryRequestMapper.map(patient));

        allPatientEventLogs.add(new PatientEventLog(patientId, PatientEvent.Suspension), "");
    }

    public Regimen currentRegimen(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        return treatmentAdvice == null ? null : allRegimens.getBy(treatmentAdvice.getRegimenId());
    }

    public PatientReport getPatientReport(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        return patientReport(patient);
    }

    public PatientReports getPatientReports(String patientId) {
        List<Patient> patients = allPatients.findAllByPatientId(patientId);
        PatientReports reports = new PatientReports();
        for (Patient patient : patients) {
            reports.add(patientReport(patient));
        }
        return reports;
    }

    public List<PatientEventLog> getStatusHistory(String patientDocId) {
        List<PatientEventLog> allPatientEventLogs = this.allPatientEventLogs.findByPatientId(patientDocId);
        return selectStatusChangeLogsAndRegimenChangeLogs(allPatientEventLogs);
    }

    private PatientReport patientReport(Patient patient) {
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patient.getId());
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        return new PatientReport(patient, earliestTreatmentAdvice, currentTreatmentAdvice, currentRegimen(patient));
    }

    private List<PatientEventLog> selectStatusChangeLogsAndRegimenChangeLogs(List<PatientEventLog> patientEventLogs) {
        Predicate<PatientEventLog> onlyStatusChangeLogsAndRegimenChangeLogs = new Predicate<PatientEventLog>() {
            @Override
            public boolean apply(PatientEventLog log) {
                List<PatientEvent> statusChangeEventsAndRegimenChangeEvents = Arrays.asList(PatientEvent.Activation,
                        PatientEvent.Suspension,
                        PatientEvent.Temporary_Deactivation,
                        PatientEvent.Regimen_Changed);
                return statusChangeEventsAndRegimenChangeEvents.contains(log.getEvent());
            }
        };

        return filter(onlyStatusChangeLogsAndRegimenChangeLogs, patientEventLogs);
    }
}