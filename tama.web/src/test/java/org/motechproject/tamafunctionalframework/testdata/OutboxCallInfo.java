package org.motechproject.tamafunctionalframework.testdata;

import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tamafunctionalframework.testdata.ivrrequest.CallInfo;
import org.motechproject.tamafunctionalframework.testdata.ivrrequest.OutgoingCallInfo;

import java.util.HashMap;

public class OutboxCallInfo implements CallInfo {
    private OutgoingCallInfo outgoingCallInfo;

    public OutboxCallInfo() {
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
        dictionary.put(TAMAIVRContext.IS_OUTBOX_CALL, "true");
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
