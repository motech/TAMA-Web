package org.motechproject.tamafunctionalframework.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.motechproject.tama.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("response")
public class IVRResponse {

    @XStreamAsAttribute
    private String sid;

    private String dial;

    @XStreamImplicit(itemFieldName = "playaudio")
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

    public boolean isNumberPresent(String number) {
        return StringUtil.ivrMobilePhoneNumber(number).equals(dial);
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
}
