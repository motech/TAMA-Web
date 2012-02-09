package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderReportService {

    public JSONObject generateJSON(String patientId, LocalDate day1, LocalDate day2) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(day1.toString(), day2.toString());
        return result;
    }
}
