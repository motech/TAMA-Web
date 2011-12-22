package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TreatmentAdviceService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private Map<CallPreference, CallPlan> callPlans;

    @Autowired
    public TreatmentAdviceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.callPlans = new HashMap<CallPreference, CallPlan>();
    }

    public void registerCallPlan(CallPreference callPreference, CallPlan callPlan) {
        this.callPlans.put(callPreference, callPlan);
    }

    public void createRegimen(TreatmentAdvice treatmentAdvice) {
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        CallPreference callPreference = patient.getPatientPreferences().getCallPreference();
        callPlans.get(callPreference).enroll(patient, treatmentAdvice);
    }

    public void changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice) {
        endCurrentRegimen(existingTreatmentAdviceId, discontinuationReason);
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        CallPreference callPreference = patient.getPatientPreferences().getCallPreference();
        callPlans.get(callPreference).reEnroll(patient, treatmentAdvice);
    }

    private void endCurrentRegimen(String treatmentAdviceId, String discontinuationReason) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        existingTreatmentAdvice.endTheRegimen(discontinuationReason);
        allTreatmentAdvices.update(existingTreatmentAdvice);
    }
}






