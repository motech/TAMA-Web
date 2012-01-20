package org.motechproject.tama.fourdayrecall.builder;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.fourdayrecall.listener.FourDayRecallListener;

import java.util.HashMap;

public class FourDayRecallEventPayloadBuilder {
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public FourDayRecallEventPayloadBuilder() {
        params.put(FourDayRecallListener.RETRY_EVENT_KEY, false);
    }

    public FourDayRecallEventPayloadBuilder withJobId(String id) {
        params.put(MotechSchedulerService.JOB_ID_KEY, id);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withPatientDocId(String patientId) {
        params.put(FourDayRecallListener.PATIENT_DOC_ID_KEY, patientId);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withRetryFlag(boolean isRetryEvent) {
        params.put(FourDayRecallListener.RETRY_EVENT_KEY, isRetryEvent);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withLastRetryDayFlagSet() {
        params.put(FourDayRecallListener.IS_LAST_RETRY_DAY, "true");
        return this;
    }

    public HashMap<String, Object> payload() {
        return params;
    }

    public FourDayRecallEventPayloadBuilder withFirstCall(boolean isFirstCall) {
        params.put(FourDayRecallListener.FIRST_CALL, isFirstCall);
        return this;
    }
}
