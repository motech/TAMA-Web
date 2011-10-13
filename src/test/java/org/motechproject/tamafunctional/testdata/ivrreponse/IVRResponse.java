package org.motechproject.tamafunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class IVRResponse {

    @XStreamAsAttribute
    private String sid;

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
