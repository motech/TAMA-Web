package org.motechproject.tama.web.view;

import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.web.tools.KooKooResponseParser;
import org.motechproject.tama.web.tools.Response;

import java.util.Map;

public class CallEventView {

    private CallEvent callEvent;

    public CallEventView(CallEvent callEvent) {
        this.callEvent = callEvent;
    }

    public String getContent() {
        Map<String, String> callEventData = callEvent.getData();
        String callEventName = callEvent.getName();

        String responseXML = callEventData.get(CallEventConstants.RESPONSE_XML);
        if (responseXML == null) {
            return "";
        }

        Response response = KooKooResponseParser.fromXml(responseXML);
        if (callEventName.equalsIgnoreCase("newcall")) {
            return response.responsePlayed() + " was played.";
        } else if (callEventName.equalsIgnoreCase("gotdtmf")) {
            return callEventData.get(CallEventConstants.DTMF_DATA) + " was pressed and " + response.responsePlayed() + " was played.";
        } else {
            return "";
        }
    }
}
