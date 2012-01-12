package org.motechproject.tamafunctional.testdata;

import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.tama.dailypillreminder.call.PillReminderCall;
import org.motechproject.tamafunctional.testdata.ivrrequest.CallInfo;
import org.motechproject.tamafunctional.testdata.ivrrequest.OutgoingCallInfo;

import java.util.HashMap;

public class FourDayRecallCallInfo implements CallInfo {
    private OutgoingCallInfo outgoingCallInfo;

    public FourDayRecallCallInfo() {
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
        outgoingCallInfo = new OutgoingCallInfo(dictionary);
    }

    @Override
    public String asString() {
        return outgoingCallInfo.asString();
    }

    @Override
    public QueryParams appendDataMapTo(QueryParams queryParams) {
        return outgoingCallInfo.appendDataMapTo(queryParams);
    }
}
