package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import java.util.Map;

public class ChangePatientPreferenceContext {
    private Map<CallPreference, CallPlan> callPlans;
    private Outbox outbox;

    public ChangePatientPreferenceContext(Map<CallPreference, CallPlan> callPlans, Outbox outbox) {
        this.callPlans = callPlans;
        this.outbox = outbox;
    }

    public void executeStrategy(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        boolean bestCallTimeChanged = dbPatient.getBestCallTime() != null && !dbPatient.getBestCallTime().equals(patient.getBestCallTime());
        boolean dayOfWeeklyCallChanged = dbPatient.isOnWeeklyPillReminder() && patient.isOnWeeklyPillReminder() && !dbPatient.getDayOfWeeklyCall().equals(patient.getDayOfWeeklyCall());
        boolean callPlanChanged = !dbPatient.callPreference().equals(patient.callPreference());

        if (callPlanChanged) {
            new CallPlanChangedStrategy(callPlans, outbox).execute(dbPatient, patient, treatmentAdvice);
        }
        if (bestCallTimeChanged) {
            new BestCallTimeChangedStrategy(callPlans, outbox).execute(dbPatient, patient, treatmentAdvice);
        }
        if (dayOfWeeklyCallChanged) {
            new DayOfWeeklyCallChangedStrategy(callPlans, outbox).execute(dbPatient, patient, treatmentAdvice);
        }
    }
}
