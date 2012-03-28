package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;

public abstract class PatientPreferenceChangedStrategy {

    protected OutboxRegistry outboxRegistry;
    protected CallPlanRegistry callPlanRegistry;

    public PatientPreferenceChangedStrategy(CallPlanRegistry callPlanRegistry, OutboxRegistry outboxRegistry) {
        this.callPlanRegistry = callPlanRegistry;
        this.outboxRegistry = outboxRegistry;
    }

    public abstract void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice);
}
