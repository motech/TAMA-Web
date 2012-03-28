package org.motechproject.tama.patient.service.registry;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.service.CallPlan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallPlanRegistry {

    private Map<CallPreference, CallPlan> callPlans = new HashMap<CallPreference, CallPlan>();

    public void registerCallPlan(CallPreference callPreference, CallPlan callPlan) {
        this.callPlans.put(callPreference, callPlan);
    }

    public CallPlan getCallPlan(CallPreference callPreference) {
        return callPlans.get(callPreference);
    }
}
