package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BestCallTimeChangedStrategy extends PatientPreferenceChangedStrategy {

    @Autowired
    public BestCallTimeChangedStrategy(CallPlanRegistry callPlanRegistry, OutboxRegistry outboxRegistry) {
        super(callPlanRegistry, outboxRegistry);
    }

    @Override
    public void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        if (dbPatient.isOnWeeklyPillReminder() && patient.isOnWeeklyPillReminder()) {
            callPlanRegistry.getCallPlan(dbPatient.callPreference()).reEnroll(patient, treatmentAdvice);
        }
        outboxRegistry.getOutbox().reEnroll(dbPatient, patient);
    }

}
