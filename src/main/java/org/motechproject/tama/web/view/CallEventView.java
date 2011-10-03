package org.motechproject.tama.web.view;

import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.web.tools.KooKooResponseParser;
import org.motechproject.tama.web.tools.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CallEventView {

    private CallEvent callEvent;

    public CallEventView(CallEvent callEvent) {
        this.callEvent = callEvent;
    }

    public List<String> getResponses() {
        Map<String, String> callEventData = callEvent.getData();

        String responseXML = callEventData.get(CallEventConstants.RESPONSE_XML);
        if (responseXML == null) {
            return Collections.emptyList();
        }

        Response response = KooKooResponseParser.fromXml(responseXML);
        return response.responsePlayed();
    }

    public boolean isUserInputAvailable() {
        return "gotdtmf".equalsIgnoreCase(callEvent.getName());
    }

    public String getUserInput(){
        Map<String, String> callEventData = callEvent.getData();
        String input = callEventData.get(CallEventConstants.DTMF_DATA);
        return input == null ? "" : input;
    }
}
