package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.TreeSet;

@RequestMapping("/json/adherence")
@Controller
public class AdherenceApiController {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public AdherenceApiController(DailyPillReminderAdherenceService dailyPillReminderAdherenceService, FourDayRecallAdherenceService fourDayRecallAdherenceService){
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(@RequestParam("id") String patientDocId) throws JSONException, NoAdherenceRecordedException {
        Map<LocalDate, Double> dailyAdherenceOverTime = dailyPillReminderAdherenceService.getAdherenceOverTime(patientDocId);
        Map<LocalDate, Double> weeklyAdherenceOverTime = fourDayRecallAdherenceService.getAdherenceOverTime(patientDocId);
        JSONArray dailyAdherencePerWeekSummary = new JSONArray();
        JSONArray weeklyAdherencePerWeekSummary = new JSONArray();
        add(dailyAdherenceOverTime, dailyAdherencePerWeekSummary);
        add(weeklyAdherenceOverTime, weeklyAdherencePerWeekSummary);

        JSONObject result = new JSONObject();
        result.put("dailyAdherenceSummary", dailyAdherencePerWeekSummary);
        result.put("weeklyAdherenceSummary", weeklyAdherencePerWeekSummary);
        return result.toString();
    }

    private void add(Map<LocalDate, Double> adherenceSummaryPerWeek, JSONArray adherencePerWeek) throws JSONException {
        for(LocalDate weekStartDate : new TreeSet<LocalDate>(adherenceSummaryPerWeek.keySet())){
            JSONObject adherenceForAWeek = new JSONObject();
            adherenceForAWeek.put("date", weekStartDate);
            adherenceForAWeek.put("percentage", adherenceSummaryPerWeek.get(weekStartDate));

            adherencePerWeek.put(adherenceForAWeek);
        }


    }

}
