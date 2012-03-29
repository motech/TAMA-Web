package org.motechproject.tama.fourdayrecall.service;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService implements CallPlan {

    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    @Autowired
    public FourDayRecallService(FourDayRecallSchedulerService fourDayRecallSchedulerService,
                                CallPlanRegistry callPlanRegistry) {
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        callPlanRegistry.registerCallPlan(CallPreference.FourDayRecall, this);
    }

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            fourDayRecallSchedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        }
    }

    public void disEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            fourDayRecallSchedulerService.unscheduleFourDayRecallJobs(patient);
        }
    }

    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        disEnroll(patient, treatmentAdvice);
        enroll(patient, treatmentAdvice);
    }
}
