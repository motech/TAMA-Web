package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.decisiontree.KookooCollectDtmfFactory;
import org.motechproject.tama.ivr.decisiontree.KookooResponseFactory;

import java.util.ArrayList;
import java.util.List;

public class IVRResponseBuilder {
    private boolean isHangUp;
    private String sid;
    private String preferredLangCode;
    private boolean collectDtmf;
    private int dtmfLength;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();

    public IVRResponseBuilder(String sid) {
    	this(sid, "en");
    }
    public IVRResponseBuilder(String sid, String preferredLangCode) {
        this.sid = sid;
        this.preferredLangCode = preferredLangCode;
    }

    public IVRResponseBuilder withPlayTexts(String... playTexts) {
        for (String playText : playTexts)
            this.playTexts.add(playText);
        return this;
    }

    public IVRResponseBuilder withPlayAudios(String... playAudios) {
        for (String playAudio : playAudios)
            this.playAudios.add(playAudio);
        return this;
    }

    public IVRResponseBuilder collectDtmf() {
        collectDtmf = true;
        return this;
    }

    public IVRResponseBuilder collectDtmf(int dtmfLength) {
        collectDtmf = true;
        this.dtmfLength = dtmfLength;
        return this;
    }

    public IVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    public Response create(IVRMessage ivrMessage) {
        Response response = KookooResponseFactory.create();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);

        if (collectDtmf) {
            CollectDtmf collectDtmf = KookooCollectDtmfFactory.create();
            if(dtmfLength > 0) collectDtmf.setMaxDigits(dtmfLength);
            for (String playText : playTexts) collectDtmf.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios) collectDtmf.addPlayAudio(ivrMessage.getWav(playAudio, preferredLangCode));

            response.addCollectDtmf(collectDtmf);
        } else {
            for (String playText : playTexts) response.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios) response.addPlayAudio(ivrMessage.getWav(playAudio, preferredLangCode));
        }
        if (isHangUp) response.addHangup();
        return response;
    }

    public boolean isHangUp() {
        return isHangUp;
    }

    public boolean isCollectDtmf() {
        return collectDtmf;
    }

    public List<String> getPlayTexts() {
        return playTexts;
    }

    public List<String> getPlayAudios() {
        return playAudios;
    }
}
