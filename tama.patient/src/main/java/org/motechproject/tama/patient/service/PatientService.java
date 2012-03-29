package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.motechproject.tama.patient.strategy.ChangedPatientPreferenceContext;
import org.motechproject.tama.patient.strategy.PatientPreferenceChangedStrategyFactory;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimens allRegimens;
    private AllPatientEventLogs allPatientEventLogs;
    private PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory;
    private OutboxRegistry outboxRegistry;

    @Autowired
    public PatientService(AllPatients allPatients,
                          AllTreatmentAdvices allTreatmentAdvices,
                          AllRegimens allRegimens,
                          AllPatientEventLogs allPatientEventLogs,
                          PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory, OutboxRegistry outboxRegistry) {

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
    }

    public void update(Patient patient, String userName) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        patient.setRegistrationDate(dbPatient.getRegistrationDate());
        patient.setActivationDate(dbPatient.getActivationDate());
        patient.setLastDeactivationDate(dbPatient.getLastDeactivationDate());
        patient.setLastSuspendedDate(dbPatient.getLastSuspendedDate());
        allPatients.update(patient, userName);

        final ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        if (changedPatientPreferenceContext.patientPreferenceHasChanged()) {
            preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext).execute(dbPatient, patient, allTreatmentAdvices.currentTreatmentAdvice(patient.getId()));
        }
    }

    public void activate(String id, String userName) {
        Patient patient = allPatients.get(id);
        patient.activate();
        allPatients.update(patient, userName);
        allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Activation, DateUtil.now()));
    }

    public void deactivate(String id, Status deactivationStatus, String userName) {
        Patient patient = allPatients.get(id);
        patient.deactivate(deactivationStatus);
        allPatients.update(patient, userName);
        if (deactivationStatus.isTemporarilyDeactivated()) {
            allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Temporary_Deactivation, DateUtil.now()));
        }
    }

    public void suspend(String patientId, String userName) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        allPatients.update(patient, userName);
        allPatientEventLogs.add(new PatientEventLog(patientId, PatientEvent.Suspension, DateUtil.now()));
    }

    public Regimen currentRegimen(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        return treatmentAdvice == null ? null : allRegimens.get(treatmentAdvice.getRegimenId());
    }

    public PatientReport getPatientReport(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientDocId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
        return new PatientReport(patient, earliestTreatmentAdvice, currentTreatmentAdvice, currentRegimen(patient));
    }
}