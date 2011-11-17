package org.motechproject.tama.web.view;

import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.web.tools.KooKooResponseParser;
import org.motechproject.tama.web.tools.Response;

import java.util.ArrayList;
import java.util.List;

public class CallEventView {
    private CallEvent callEvent;

    public CallEventView(CallEvent callEvent) {
        this.callEvent = callEvent;
    }

    public List<String> getResponses() {
        CallEventCustomData customData = callEvent.getData();
        List<String> responseXMLs = customData.getAll(CallEventConstants.CUSTOM_DATA_LIST);
        ArrayList<String> responses = new ArrayList<String>();
        if (responseXMLs == null) {
            return responses;
        }

        for (String responseXML : responseXMLs) {
            Response response = KooKooResponseParser.fromXml(responseXML);
            List<String> audios = response.responsePlayed();
            responses.addAll(audios);
        }
        return responses;
    }

    public boolean isUserInputAvailable() {
        return "gotdtmf".equalsIgnoreCase(callEvent.getName());
    }

    public String getUserInput() {
        CallEventCustomData customData = callEvent.getData();
        String input = customData.getFirst(CallEventConstants.DTMF_DATA);
        return input == null ? "" : input;
    }
}
