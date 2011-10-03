package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.Collections;
import java.util.List;

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

    public List<String> responsePlayed() {
        if (collectDtmf()) {
            return collectdtmf.responsePlayed();
        }
        return Collections.emptyList();
    }
}

