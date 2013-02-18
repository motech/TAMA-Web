package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.ivr.log.tools.KooKooResponseParser;
import org.motechproject.tama.ivr.log.tools.Response;

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

    public boolean isMissedCall() {
        return "missed".equalsIgnoreCase(callEvent.getName());
    }

    public String getUserInput() {
        return getData(CallEventConstants.DTMF_DATA);
    }

    public String getTree() {
        return getData(CallEventConstants.TREE_NAME);
    }

    public String getCallState() {
        return getData(CallEventConstants.CALL_STATE);
    }

    public String getCallType() {
        return getData(IVRService.CALL_TYPE);
    }

    public DateTime getTimeStamp(){
        return callEvent.getTimeStamp();
    }

    private String getData(String key) {
        CallEventCustomData customData = callEvent.getData();
        String data = customData.getFirst(key);
        return data == null ? "" : data;
    }
}
