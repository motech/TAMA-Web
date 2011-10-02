package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class Response {
    @XStreamAsAttribute
    private String sid;

    private CollectDtmf collectdtmf;

    public String sid() {
        return sid;
    }

    public boolean collectDtmf() {
        return collectdtmf != null;
    }

    public String responsePlayed() {
        if (collectDtmf()) {
            return collectdtmf.responsePlayed();
        }
        return "";
    }
}

