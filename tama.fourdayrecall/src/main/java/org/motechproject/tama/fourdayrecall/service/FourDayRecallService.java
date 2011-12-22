package org.motechproject.tama.fourdayrecall.service;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.FourDayRecall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService extends FourDayRecall {

    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    @Autowired
    public FourDayRecallService(TreatmentAdviceService treatmentAdviceService) {
        treatmentAdviceService.registerFourDayRecall(this);
    }

    @Override
    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        fourDayRecallSchedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

    @Override
    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        fourDayRecallSchedulerService.rescheduleFourDayRecallJobs(patient, treatmentAdvice);
    }

}
