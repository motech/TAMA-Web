package org.motechproject.tama.fourdayrecall.service;

import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallService implements CallPlan {

    private FourDayRecallSchedulerService fourDayRecallSchedulerService;
    private AllPatientEventLogs allPatientEventLogs;

    @Autowired
    public FourDayRecallService(FourDayRecallSchedulerService fourDayRecallSchedulerService,
                                AllPatientEventLogs allPatientEventLogs, CallPlanRegistry callPlanRegistry) {
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        this.allPatientEventLogs = allPatientEventLogs;
        callPlanRegistry.registerCallPlan(CallPreference.FourDayRecall, this);
    }

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice) {
        if (treatmentAdvice != null) {
            fourDayRecallSchedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        }
        allPatientEventLogs.add(new PatientEventLog(patient.getId(), PatientEvent.Switched_To_Weekly_Adherence, DateUtil.now()));
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
