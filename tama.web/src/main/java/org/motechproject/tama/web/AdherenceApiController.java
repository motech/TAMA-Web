package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RequestMapping("/json/adherence")
@Controller
public class AdherenceApiController {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    @Autowired
    public AdherenceApiController(DailyPillReminderAdherenceService dailyPillReminderAdherenceService){
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(@RequestParam("id") String patientDocId) throws JSONException {
        Map<LocalDate, Double> adherenceSummaryPerWeek = dailyPillReminderAdherenceService.getAdherenceOverTime(patientDocId);
        JSONArray adherencePerWeek = new JSONArray();
        for(LocalDate weekStartDate : new TreeSet<LocalDate>(adherenceSummaryPerWeek.keySet())){
            JSONObject adherenceForAWeek = new JSONObject();
            adherenceForAWeek.put("date", weekStartDate);
            adherenceForAWeek.put("percentage", adherenceSummaryPerWeek.get(weekStartDate));

            adherencePerWeek.put(adherenceForAWeek);
        }

        JSONObject result = new JSONObject();
        result.put("adherencePerWeek", adherencePerWeek);
        return result.toString();
    }

}
