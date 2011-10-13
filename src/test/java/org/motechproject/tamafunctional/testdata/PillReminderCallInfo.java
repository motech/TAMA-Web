package org.motechproject.tamafunctional.testdata;

import org.json.JSONObject;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tamafunctional.testdata.ivrrequest.CallInfo;

import java.util.Map;
import java.util.HashMap;

public class PillReminderCallInfo implements CallInfo {
    private HashMap<String, String> dictionary;

    public PillReminderCallInfo(String dosageId, int callNumber) {
        dictionary = new HashMap<String, String>();
        dictionary.put(PillReminderCall.DOSAGE_ID, dosageId);
        dictionary.put(PillReminderCall.TIMES_SENT, Integer.valueOf(callNumber).toString());
        dictionary.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "5");
    }

    @Override
    public String asQueryParameter() {
        return new JSONObject(dictionary).toString();
    }

    @Override
    public CallInfo outgoingCall() {
        dictionary.put(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
        return this;
    }

    @Override
    public String appendDataMapTo(String url) {
        return String.format("%s&dataMap=%s", url, asQueryParameter());
    }
}
