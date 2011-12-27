package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import java.util.Map;

public abstract class ChangePatientPreferenceStrategy {
    protected Map<CallPreference, CallPlan> callPlans;
    protected Outbox outbox;

    public ChangePatientPreferenceStrategy(Map<CallPreference, CallPlan> callPlans, Outbox outbox) {
        this.callPlans = callPlans;
        this.outbox = outbox;
    }

    public abstract void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice);
}
