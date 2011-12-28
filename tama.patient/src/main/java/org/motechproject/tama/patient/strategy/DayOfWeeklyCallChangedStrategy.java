package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import java.util.Map;

public class DayOfWeeklyCallChangedStrategy extends ChangePatientPreferenceStrategy {
    public DayOfWeeklyCallChangedStrategy(Map<CallPreference, CallPlan> callPlans, Outbox outbox) {
        super(callPlans, outbox);
    }

    @Override
    public void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        callPlans.get(dbPatient.callPreference()).reEnroll(patient, treatmentAdvice);
    }
}
