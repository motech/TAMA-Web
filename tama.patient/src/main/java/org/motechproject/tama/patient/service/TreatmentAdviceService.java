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
import java.util.List;
import java.util.Map;

@Component
public class TreatmentAdviceService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private CallTimeSlotService callTimeSlotService;
    private Map<CallPreference, CallPlan> callPlans;

    @Autowired
    public TreatmentAdviceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, CallTimeSlotService callTimeSlotService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.callTimeSlotService = callTimeSlotService;
        this.callPlans = new HashMap<CallPreference, CallPlan>();
    }

    public void registerCallPlan(CallPreference callPreference, CallPlan callPlan) {
        this.callPlans.put(callPreference, callPlan);
    }

    public String createRegimen(TreatmentAdvice treatmentAdvice) {
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        if (patient.isOnDailyPillReminder()) {
            callTimeSlotService.allotSlots(patient, treatmentAdvice);
        }
        callPlans.get(patient.callPreference()).enroll(patient, treatmentAdvice);
        return treatmentAdvice.getId();
    }

    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice) {
        endCurrentRegimen(existingTreatmentAdviceId, discontinuationReason);
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        callPlans.get(patient.callPreference()).reEnroll(patient, treatmentAdvice);
        return treatmentAdvice.getId();
    }

    private void endCurrentRegimen(String treatmentAdviceId, String discontinuationReason) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        existingTreatmentAdvice.endTheRegimen(discontinuationReason);
        allTreatmentAdvices.update(existingTreatmentAdvice);
    }
}