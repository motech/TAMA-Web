package org.motechproject.tamafunctional.testdata;

import org.json.JSONObject;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tamafunctional.testdata.ivrrequest.CallInfo;

import java.util.Map;
import java.util.HashMap;

public class PillReminderCallInfo implements CallInfo {
    private String dosageId;
    private int callNumber;

    public PillReminderCallInfo(String dosageId, int callNumber) {
        this.dosageId = dosageId;
        this.callNumber = callNumber;
    }

    @Override
    public String asQueryParameter() {
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(PillReminderCall.DOSAGE_ID, dosageId);
        dictionary.put(PillReminderCall.TIMES_SENT, Integer.valueOf(callNumber).toString());
        dictionary.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "5");
        return new JSONObject(dictionary).toString();
    }
}
