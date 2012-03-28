package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DayOfWeeklyCallChangedStrategy extends PatientPreferenceChangedStrategy {

    @Autowired
    public DayOfWeeklyCallChangedStrategy(CallPlanRegistry callPlanRegistry, OutboxRegistry outbox) {
        super(callPlanRegistry, outbox);
    }

    @Override
    public void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        callPlanRegistry.getCallPlan(dbPatient.callPreference()).reEnroll(patient, treatmentAdvice);
    }

}
