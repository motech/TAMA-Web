package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TreatmentAdviceService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private CallTimeSlotService callTimeSlotService;
    private CallPlanRegistry callPlanRegistry;

    @Autowired
    public TreatmentAdviceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, CallTimeSlotService callTimeSlotService, CallPlanRegistry callPlanRegistry) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.callTimeSlotService = callTimeSlotService;
        this.callPlanRegistry = callPlanRegistry;
    }

    public String createRegimen(TreatmentAdvice treatmentAdvice, String userName) {
        allTreatmentAdvices.add(treatmentAdvice, userName);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        if (patient.isOnDailyPillReminder()) {
            callTimeSlotService.allotSlots(patient, treatmentAdvice);
        }
        callPlanRegistry.getCallPlan(patient.callPreference()).enroll(patient, treatmentAdvice);
        return treatmentAdvice.getId();
    }

    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice, String userName) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(existingTreatmentAdviceId);
        endCurrentRegimen(discontinuationReason, existingTreatmentAdvice, userName);
        allTreatmentAdvices.add(treatmentAdvice, userName);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        callTimeSlotService.freeSlots(patient, existingTreatmentAdvice);
        if (patient.isOnDailyPillReminder()) {
            callTimeSlotService.allotSlots(patient, treatmentAdvice);
        }
        callPlanRegistry.getCallPlan(patient.callPreference()).reEnroll(patient, treatmentAdvice);
        return treatmentAdvice.getId();
    }

    private void endCurrentRegimen(String discontinuationReason, TreatmentAdvice existingTreatmentAdvice, String userName) {
        existingTreatmentAdvice.endTheRegimen(discontinuationReason);
        allTreatmentAdvices.update(existingTreatmentAdvice, userName);
    }
}