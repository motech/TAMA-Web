package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.listener.FourDayRecallListener;

import java.util.HashMap;

public class FourDayRecallEventPayloadBuilder {
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public FourDayRecallEventPayloadBuilder withJobId(String id) {
        params.put(MotechSchedulerService.JOB_ID_KEY, id);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withPatientDocId(String patientId) {
        params.put(FourDayRecallListener.PATIENT_DOC_ID_KEY, patientId);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withStartDate(LocalDate startDate) {
        params.put(FourDayRecallListener.START_DATE, startDate);
        return this;
    }

    public FourDayRecallEventPayloadBuilder withEndDate(LocalDate startDate) {
        params.put(FourDayRecallListener.END_DATE, startDate);
        return this;
    }

    public HashMap<String, Object> payload() {
        return params;
    }
}
