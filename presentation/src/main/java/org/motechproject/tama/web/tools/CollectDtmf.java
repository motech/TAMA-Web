package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectDtmf {

    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();

    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    private boolean isPlayAudio;
    private boolean isPlayText;

    public boolean isPlayAudio() {
        return isPlayAudio;
    }

    public void setPlayAudio(boolean playAudio) {
        isPlayAudio = playAudio;
    }

    public boolean isPlayText() {
        return isPlayText;
    }

    public void setPlayText(boolean playText) {
        isPlayText = playText;
    }

    public List<String> responsePlayed() {
        if(playaudios != null){
            setPlayAudio(true);
            return playaudios;
        }else if(playtexts != null){
            setPlayText(true);
            return playtexts;
        }else {
            return Collections.emptyList();
        }
    }

}
