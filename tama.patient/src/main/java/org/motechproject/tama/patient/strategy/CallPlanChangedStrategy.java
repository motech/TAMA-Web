package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import java.util.Map;

public class CallPlanChangedStrategy extends ChangePatientPreferenceStrategy {
    public CallPlanChangedStrategy(Map<CallPreference, CallPlan> callPlans, Outbox outbox) {
        super(callPlans, outbox);
    }

    @Override
    public void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        patient.getPatientPreferences().setCallPreferenceTransitionDate(DateUtil.now());
        callPlans.get(dbPatient.callPreference()).disEnroll(dbPatient, treatmentAdvice);
        callPlans.get(patient.callPreference()).enroll(patient, treatmentAdvice);
        outbox.reEnroll(dbPatient, patient);
    }
}
