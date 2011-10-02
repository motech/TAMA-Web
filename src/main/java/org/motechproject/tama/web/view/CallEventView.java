package org.motechproject.tama.web.view;

import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.web.tools.KooKooResponseParser;
import org.motechproject.tama.web.tools.Response;

import java.util.Map;

public class CallEventView {

    private String name;

    private Map<String, String> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }

    public String getContent(){
        String responseXML = data.get(CallEventConstants.RESPONSE_XML);
        if(responseXML == null){
            return "";
        }

        Response response = KooKooResponseParser.fromXml(responseXML);
        if(name.equalsIgnoreCase("newcall")){
            return response.audioPlayed() + " was played.";
        }
        else if (name.equalsIgnoreCase("gotdtmf")){
            return data.get(CallEventConstants.DTMF_DATA) + " was pressed and " + response.audioPlayed() + " was played.";
        }
        else {
            return "";
        }
    }
}
