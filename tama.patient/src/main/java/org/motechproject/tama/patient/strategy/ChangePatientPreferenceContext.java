package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import java.util.Map;

public class ChangePatientPreferenceContext {
    private Map<CallPreference, CallPlan> callPlans;
    private Outbox outbox;

    public ChangePatientPreferenceContext(Map<CallPreference, CallPlan> callPlans, Outbox outbox) {
        this.callPlans = callPlans;
        this.outbox = outbox;
    }

    public ChangePatientPreferenceStrategy getStrategy(Patient dbPatient, Patient patient) {
        boolean callPlanChanged = !dbPatient.callPreference().equals(patient.callPreference());
        boolean bestCallTimeChanged = dbPatient.getBestCallTime() != null && !dbPatient.getBestCallTime().equals(patient.getBestCallTime());
        boolean dayOfWeeklyCallChanged = dbPatient.getDayOfWeeklyCall() != null && !dbPatient.getDayOfWeeklyCall().equals(patient.getDayOfWeeklyCall());

        if (callPlanChanged) {
            return new CallPlanChangedStrategy(callPlans, outbox);
        }
        else if (bestCallTimeChanged) {
            return new BestCallTimeChangedStrategy(callPlans, outbox);
        }
        else if (dayOfWeeklyCallChanged) {
            return new DayOfWeeklyCallChangedStrategy(callPlans, outbox);
        }
        return null;
    }
}
