package org.motechproject.tama.symptomreporting.controller;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/json/symptoms")
public class SymptomReportingChartController {
	
    @Autowired
    AllSymptomReports allSymptomReports;
    
    @Autowired @Qualifier("symptomProperties") 
    Properties symptomTable;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(@RequestParam("id") String patientDocId, @RequestParam("rangeInMonths") Integer rangeInMonths) throws JSONException {
        LocalDate today = DateUtil.today().plusDays(1);
        LocalDate from = today.minusMonths(rangeInMonths);
        List<SymptomReport> symptomReports = allSymptomReports.getSymptomReports(patientDocId, from, today);
        JSONArray events = new JSONArray();
        Map<String, DateTime> lastReportedAtMap = new HashMap<String, DateTime>();
        TrackNumberGenerator trackNumberGenerator = new TrackNumberGenerator();
        for(int i=symptomReports.size()-1; i>=0; i--){
            SymptomReport report = symptomReports.get(i);
        	for (String symptomId : report.getSymptomIds()) {
	            JSONObject event = new JSONObject();
	            event.put("start", report.getReportedAt());
	            if (lastReportedAtMap.get(symptomId) == null || report.getReportedAt().isBefore(lastReportedAtMap.get(symptomId).minusDays(7)))
                    event.put("title", lookupSymptom(symptomId));
	            event.put("durationEvent", false);
	            event.put("trackNum", trackNumberGenerator.trackNumberFor(symptomId));
	            event.put("description", lookupDesc(symptomId));
	            events.put(event);
                lastReportedAtMap.put(symptomId,report.getReportedAt());
        	}
        }
        JSONObject result = new JSONObject();
        result.put("events", events);
        result.put("dateTimeFormat", "Gregorian");
        return result.toString();
    }

	class TrackNumberGenerator{
    	int nextAvailableTrack = 1;
    	Map<String, Integer> tracksUsed = new HashMap<String, Integer>(); 
		private int trackNumberFor(String symptom) {
			if (tracksUsed.get(symptom) == null) {
				tracksUsed.put(symptom, nextAvailableTrack); 
				nextAvailableTrack ++;
			}
			return tracksUsed.get(symptom);
		}
    }

    private String lookupSymptom(String symptomId) {
        return symptomTable.getProperty(symptomId);
    }

    private String lookupDesc(String symptomId) {
    	return symptomTable.getProperty(symptomId + ".desc");
	}
}
