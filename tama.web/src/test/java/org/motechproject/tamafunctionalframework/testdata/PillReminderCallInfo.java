package org.motechproject.tamafunctionalframework.testdata;

import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.tama.dailypillreminder.call.PillReminderCall;
import org.motechproject.tamafunctionalframework.testdata.ivrrequest.CallInfo;
import org.motechproject.tamafunctionalframework.testdata.ivrrequest.OutgoingCallInfo;

import java.util.HashMap;

public class PillReminderCallInfo implements CallInfo {
    private OutgoingCallInfo outgoingCallInfo;

    public PillReminderCallInfo(int callNumber) {
        HashMap<String, String> pillReminderCallParameters = new HashMap<String, String>();
        pillReminderCallParameters.put(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
        pillReminderCallParameters.put(PillReminderCall.TIMES_SENT, Integer.valueOf(callNumber).toString());
        pillReminderCallParameters.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "5");
        pillReminderCallParameters.put(PillReminderCall.RETRY_INTERVAL, "15");
        outgoingCallInfo = new OutgoingCallInfo(pillReminderCallParameters);
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
