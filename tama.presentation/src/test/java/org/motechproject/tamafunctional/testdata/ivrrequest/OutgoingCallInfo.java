package org.motechproject.tamafunctional.testdata.ivrrequest;

import org.json.JSONObject;
import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;

import java.util.HashMap;

public class OutgoingCallInfo implements CallInfo {
    private HashMap<String, String> dictionary;

    public OutgoingCallInfo(HashMap<String, String> dictionary) {
        this.dictionary = dictionary;
        dictionary.put(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
    }

    @Override
    public String asString() {
        return new JSONObject(dictionary).toString();
    }

    @Override
    public QueryParams appendDataMapTo(QueryParams queryParams) {
        return queryParams.put("dataMap", asString());
    }
}
