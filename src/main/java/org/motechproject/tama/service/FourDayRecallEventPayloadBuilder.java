package org.motechproject.tama.service;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.listener.FourDayRecallListener;

import java.util.HashMap;

public class FourDayRecallEventPayloadBuilder {

    private HashMap<String, Object> params = new HashMap<String, Object>();

    public FourDayRecallEventPayloadBuilder withJobId(String id) {
        params.put(MotechSchedulerService.JOB_ID_KEY, id);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withPatientId(String patientId) {
        params.put(FourDayRecallListener.PATIENT_ID_KEY, patientId);
        return this;
    }

    public HashMap<String, Object> payload() {
        return params;
    }
}
