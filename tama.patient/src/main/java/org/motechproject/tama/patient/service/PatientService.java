package org.motechproject.tama.patient.service;

import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.strategy.CallPlan;
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
    private Map<CallPreference, CallPlan> callPlans;
    private Outbox outbox;

    @Autowired
    public PatientService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllUniquePatientFields allUniquePatientFields) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allUniquePatientFields = allUniquePatientFields;
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
        updateUniquePatientField(patient);
        updateOnCallPlanChanged(dbPatient, patient);
        outbox.reEnroll(dbPatient, patient);
        allPatients.update(patient);
    }

    public void suspend(String patientId) {
        Patient patient = allPatients.get(patientId);
        patient.suspend();
        allPatients.update(patient);
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

    private void updateOnCallPlanChanged(Patient dbPatient, Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        boolean callPlanChanged = !dbPatient.callPreference().equals(patient.callPreference());
        if (callPlanChanged) {
            callPlans.get(dbPatient.callPreference()).disEnroll(patient);
            callPlans.get(patient.callPreference()).enroll(patient, treatmentAdvice);
            patient.getPatientPreferences().setCallPreferenceTransitionDate(DateUtil.now());
        }
    }
}






