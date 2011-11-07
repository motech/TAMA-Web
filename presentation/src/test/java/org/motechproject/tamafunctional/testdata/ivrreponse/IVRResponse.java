package org.motechproject.tamafunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang.StringUtils;

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

    public boolean wasAudioPlayed(String... names) {
        return (collectDtmf() && collectdtmf.hasAudio(names)) || new Audios(playaudios).hasAudio(names);
    }

    public String audiosPlayed() {
        return collectDtmf() ? collectdtmf.audiosPlayed() : new Audios(playaudios).toString();
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
