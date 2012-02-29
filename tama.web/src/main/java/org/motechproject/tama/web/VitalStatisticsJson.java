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
            if(vitalStatistics.getWeightInKg() != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", vitalStatistics.getCaptureDate().toString(TAMAConstants.DATE_FORMAT));
                jsonObject.put("value", vitalStatistics.getWeightInKg());
                weightListJson.put(jsonObject);
            }
        }

        return weightListJson;
    }

    public JSONArray bpList() throws JSONException  {
        JSONArray weightListJson = new JSONArray();
        for (VitalStatistics vitalStatistics : vitalStatisticsList) {
            if(vitalStatistics.getSystolicBp() != null || vitalStatistics.getDiastolicBp() != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", vitalStatistics.getCaptureDate().toString(TAMAConstants.DATE_FORMAT));
                jsonObject.put("systolic", vitalStatistics.getSystolicBp());
                jsonObject.put("diastolic", vitalStatistics.getDiastolicBp());
                weightListJson.put(jsonObject);
            }
        }

        return weightListJson;
    }
}
