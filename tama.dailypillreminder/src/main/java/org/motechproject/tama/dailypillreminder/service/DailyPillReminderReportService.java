package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderReportService {

    public JSONObject createReport(String patientId, LocalDate startDate, LocalDate endDate) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(startDate.toString(), endDate.toString());
        return result;
    }
}
