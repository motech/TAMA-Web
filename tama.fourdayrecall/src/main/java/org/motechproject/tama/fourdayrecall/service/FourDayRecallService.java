package org.motechproject.tama.fourdayrecall.service;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService implements CallPlan {

    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    @Autowired
    public FourDayRecallService(FourDayRecallSchedulerService fourDayRecallSchedulerService, TreatmentAdviceService treatmentAdviceService) {
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        treatmentAdviceService.registerCallPlan(CallPreference.FourDayRecall, this);
    }

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        fourDayRecallSchedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        fourDayRecallSchedulerService.rescheduleFourDayRecallJobs(patient, treatmentAdvice);
    }
}
