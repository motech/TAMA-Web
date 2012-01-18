package org.motechproject.tama.patient.service;

import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.motechproject.tama.patient.strategy.ChangePatientPreferenceContext;
import org.motechproject.tama.patient.strategy.ChangePatientPreferenceStrategy;
import org.motechproject.tama.patient.strategy.Outbox;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllUniquePatientFields allUniquePatientFields;
    private AllPatientEventLogs allPatientEventLogs;
    private Map<CallPreference, CallPlan> callPlans;
    private Outbox outbox;

    @Autowired
    public PatientService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllUniquePatientFields allUniquePatientFields, AllPatientEventLogs allPatientEventLogs) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allUniquePatientFields = allUniquePatientFields;
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
        updateUniquePatientField(patient);
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

    private void updateUniquePatientField(Patient patient) {
        List<UniquePatientField> oldUniquePatientFields = allUniquePatientFields.get(patient);
        allUniquePatientFields.remove(patient);
        try {
            allUniquePatientFields.add(patient);
        } catch (TamaException e) {
            for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                allUniquePatientFields.add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
            }
            throw e;
        }
    }

    private void updateOnPatientPreferencesChanged(Patient dbPatient, Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        ChangePatientPreferenceStrategy changePatientPreferenceStrategy = new ChangePatientPreferenceContext(callPlans, outbox).getStrategy(dbPatient, patient);
        if (changePatientPreferenceStrategy != null) {
            changePatientPreferenceStrategy.execute(dbPatient, patient, treatmentAdvice);
        }
    }
}