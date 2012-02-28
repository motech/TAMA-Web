package org.motechproject.tama.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.VitalStatistics;

import java.util.List;

public class VitalStatisticsJson {

    private List<VitalStatistics> vitalStatisticsList;

    public VitalStatisticsJson(List<VitalStatistics> vitalStatisticsList) {
        this.vitalStatisticsList = vitalStatisticsList;
    }

    public JSONArray weightList() throws JSONException {
        JSONArray weightListJson = new JSONArray();
        for (VitalStatistics vitalStatistics : vitalStatisticsList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", vitalStatistics.getCaptureDate().toString(TAMAConstants.DATE_FORMAT));
            jsonObject.put("value", vitalStatistics.getWeightInKg());
            weightListJson.put(jsonObject);
        }

        return weightListJson;
    }
}
