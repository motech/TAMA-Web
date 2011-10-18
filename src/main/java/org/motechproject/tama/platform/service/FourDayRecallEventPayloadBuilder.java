package org.motechproject.tama.platform.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.listener.FourDayRecallListener;

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

    public FourDayRecallEventPayloadBuilder withTreatmentAdviceStartDate(LocalDate startDate) {
        params.put(FourDayRecallListener.TREATMENT_ADVICE_START_DATE_KEY, startDate);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withTreatmentAdviceId(String treatmentAdviceId) {
        params.put(FourDayRecallListener.TREATMENT_ADVICE_DOC_ID_KEY, treatmentAdviceId);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withRetryFlag(boolean isRetryEvent) {
        params.put(FourDayRecallListener.RETRY_EVENT_KEY, isRetryEvent);
        return this;
    }

    public HashMap<String, Object> payload() {
        return params;
    }
}
