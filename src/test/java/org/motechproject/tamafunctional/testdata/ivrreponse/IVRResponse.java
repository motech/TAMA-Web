package org.motechproject.tamafunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("response")
public class IVRResponse {

    @XStreamAsAttribute
    private String sid;

    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();

    private Hangup hangup;

    private CollectDtmf collectdtmf;

    public String sid() {
        return sid;
    }

    public boolean isHangedUp() {
        return hangup != null;
    }

    public boolean collectDtmf() {
        return collectdtmf != null;
    }

    public boolean audioPlayed(String ... names) {
        return collectDtmf() && collectdtmf.hasAudio(names);
    }

    public String audiosPlayed() {
        if (collectDtmf()) {
            return collectdtmf.audiosPlayed();
        }
        return "";
    }

    public boolean isEmpty() {
        return !collectDtmf() && !isHangedUp();
    }

    public IVRResponse addAudio(String audioLocation) {
        if (collectdtmf == null) collectdtmf = new CollectDtmf();
        collectdtmf.playAudios(audioLocation);
        return this;
    }
}
