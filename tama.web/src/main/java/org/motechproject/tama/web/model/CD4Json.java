package org.motechproject.tama.web.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.LabResult;

import java.util.List;

public class CD4Json extends JSONArray {

    public CD4Json(List<LabResult> defaultCD4Results) throws JSONException {
        for (LabResult labResult : defaultCD4Results) {
            put(createResult(labResult));
        }
    }

    private JSONObject createResult(LabResult labResult) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", labResult.getTestDate().toString(TAMAConstants.DATE_FORMAT));
        jsonObject.put("value", labResult.getResult());
        return jsonObject;
    }
}
