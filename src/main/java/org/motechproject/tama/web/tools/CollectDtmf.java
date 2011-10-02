package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

public class CollectDtmf {

    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();

    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    public String responsePlayed() {
        if(playaudios != null){
            return playaudios.get(0);
        }else if(playtexts != null){
            return playtexts.get(0);
        }else {
            return "";
        }
    }

}
