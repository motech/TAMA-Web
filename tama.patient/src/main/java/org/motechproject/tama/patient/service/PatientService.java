package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.motechproject.tama.patient.strategy.ChangePatientPreferenceContext;
import org.motechproject.tama.patient.strategy.ChangePatientPreferenceStrategy;
import org.motechproject.tama.patient.strategy.Outbox;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimens allRegimens;
    private AllPatientEventLogs allPatientEventLogs;
    private Map<CallPreference, CallPlan> callPlans;
    private Outbox outbox;

    @Autowired
    public PatientService(AllPatients allPatients,
                          AllTreatmentAdvices allTreatmentAdvices,
                          AllRegimens allRegimens,
                          AllPatientEventLogs allPatientEventLogs) {

        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
        this.allPatientEventLogs = allPatientEventLogs;
        this.callPlans = new HashMap<CallPreference, CallPlan>();
    }

    public void registerCallPlan(CallPreference callPreference, CallPlan callPlan) {
        this.callPlans.put(callPreference, callPlan);
    }

    public void registerOutbox(Outbox outbox) {
        this.outbox = outbox;
    }

    public void create(Patient patient, String clinicId) {
        allPatients.addToClinic(patient, clinicId);
        outbox.enroll(patient);
    }

    public void update(Patient patient) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());
        patient.setRegistrationDate(dbPatient.getRegistrationDate());
        patient.setActivationDate(dbPatient.getActivationDate());
        patient.setLastDeactivationDate(dbPatient.getLastDeactivationDate());
        patient.setLastSuspendedDate(dbPatient.getLastSuspendedDate());
        allPatients.update(patient);
        updateOnPatientPreferencesChanged(dbPatient, patient);
    }

    public void activate(String id) {
        Patient patient = allPatients.get(id);
        patient.activate();
        allPatients.update(patient);
        allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Activation, DateUtil.now()));
    }

    public void deactivate(String id, Status deactivationStatus) {
        Patient patient = allPatients.get(id);
        patient.deactivate(deactivationStatus);
        allPatients.update(patient);
        if(deactivationStatus.isTemporarilyDeactivated()) {
            allPatientEventLogs.add(new PatientEventLog(id, PatientEvent.Temporary_Deactivation, DateUtil.now()));
        }
    }

    public void suspend(String patientId) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        allPatients.update(patient);
        allPatientEventLogs.add(new PatientEventLog(patientId, PatientEvent.Suspension, DateUtil.now()));
    }

    private void updateOnPatientPreferencesChanged(Patient dbPatient, Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        ChangePatientPreferenceStrategy changePatientPreferenceStrategy = new ChangePatientPreferenceContext(callPlans, outbox).getStrategy(dbPatient, patient);
        if (changePatientPreferenceStrategy != null) {
            changePatientPreferenceStrategy.execute(dbPatient, patient, treatmentAdvice);
        }
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