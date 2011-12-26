package org.motechproject.tama.fourdayrecall.service;

import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService implements CallPlan {

    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    @Autowired
    public FourDayRecallService(FourDayRecallSchedulerService fourDayRecallSchedulerService, PatientService patientService, TreatmentAdviceService treatmentAdviceService) {
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        patientService.registerCallPlan(CallPreference.FourDayRecall, this);
        treatmentAdviceService.registerCallPlan(CallPreference.FourDayRecall, this);
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
