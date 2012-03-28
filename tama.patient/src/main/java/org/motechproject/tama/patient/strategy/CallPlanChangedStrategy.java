package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallPlanChangedStrategy extends PatientPreferenceChangedStrategy {

    @Autowired
    public CallPlanChangedStrategy(CallPlanRegistry callPlanRegistry, OutboxRegistry outboxRegistry) {
        super(callPlanRegistry, outboxRegistry);
    }

    @Override
    public void execute(Patient dbPatient, Patient patient, TreatmentAdvice treatmentAdvice) {
        patient.getPatientPreferences().setCallPreferenceTransitionDate(DateUtil.now());
        callPlanRegistry.getCallPlan(dbPatient.callPreference()).disEnroll(dbPatient, treatmentAdvice);
        callPlanRegistry.getCallPlan(patient.callPreference()).enroll(patient, treatmentAdvice);
        outboxRegistry.getOutbox().reEnroll(dbPatient, patient);
    }

}
