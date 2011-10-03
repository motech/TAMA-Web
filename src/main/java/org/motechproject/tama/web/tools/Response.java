package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("response")
public class Response {

    @XStreamAsAttribute
    private String sid;

    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();

    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    private CollectDtmf collectdtmf;

    @XStreamAlias("hangup")
    private HangUp hangUp;

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
        return getResponsesPlayed();
    }

    private List<String> getResponsesPlayed(){
        if(playaudios!= null && !playaudios.isEmpty()){
            return playaudios;
        }
        if(playtexts!= null && !playtexts.isEmpty()){
            return playtexts;
        }
        return Collections.emptyList();
    }
}

