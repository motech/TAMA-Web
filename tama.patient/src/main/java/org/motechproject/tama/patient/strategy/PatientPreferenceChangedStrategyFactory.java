package org.motechproject.tama.patient.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientPreferenceChangedStrategyFactory {

    CallPlanChangedStrategy callPlanChangedStrategy;
    BestCallTimeChangedStrategy bestCallTimeChangedStrategy;
    DayOfWeeklyCallChangedStrategy dayOfWeeklyCallChangedStrategy;

    @Autowired
    public PatientPreferenceChangedStrategyFactory(CallPlanChangedStrategy callPlanChangedStrategy, BestCallTimeChangedStrategy bestCallTimeChangedStrategy, DayOfWeeklyCallChangedStrategy dayOfWeeklyCallChangedStrategy) {
        this.callPlanChangedStrategy = callPlanChangedStrategy;
        this.bestCallTimeChangedStrategy = bestCallTimeChangedStrategy;
        this.dayOfWeeklyCallChangedStrategy = dayOfWeeklyCallChangedStrategy;
    }

    public PatientPreferenceChangedStrategy getStrategy(ChangedPatientPreferenceContext context) {
        if (context.callPlanHasChanged()) {
            return callPlanChangedStrategy;
        } else if (context.bestCallTimeHasChanged()) {
            return bestCallTimeChangedStrategy;
        } else if (context.dayOfCallHasChanged()) {
            return dayOfWeeklyCallChangedStrategy;
        }
        return null;
    }

}
