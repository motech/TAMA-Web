package org.motechproject.tama.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.domain.AdherenceSummaryForAWeek;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
        JSONObject result = new JSONObject();
        result.put("dailyAdherenceSummary", jsonify(dailyPillReminderAdherenceService.getAdherenceOverTime(patientDocId)));
        result.put("weeklyAdherenceSummary", jsonify(fourDayRecallAdherenceService.getAdherenceOverTime(patientDocId)));
        return result.toString();
    }

    private JSONArray jsonify(List<AdherenceSummaryForAWeek> adherenceSummaryPerWeek) throws JSONException {
        JSONArray adherencePerWeekSummaryJSON = new JSONArray();
        for(AdherenceSummaryForAWeek adherenceSummaryForAWeek: adherenceSummaryPerWeek){
            JSONObject adherenceSummaryForAWeekJSON = new JSONObject();
            adherenceSummaryForAWeekJSON.put("date", adherenceSummaryForAWeek.getWeekStartDate().toLocalDate());
            adherenceSummaryForAWeekJSON.put("percentage", adherenceSummaryForAWeek.getPercentage());
            adherenceSummaryForAWeekJSON.put("taken", adherenceSummaryForAWeek.getTaken());
            adherenceSummaryForAWeekJSON.put("total", adherenceSummaryForAWeek.getTotal());

            adherencePerWeekSummaryJSON.put(adherenceSummaryForAWeekJSON);
        }
        return adherencePerWeekSummaryJSON;
    }
}
