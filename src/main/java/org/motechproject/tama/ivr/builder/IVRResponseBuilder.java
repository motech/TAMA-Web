package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IVRResponseBuilder {
    private boolean isHangUp;
    private String sid;
    private CollectDtmf collectDtmf;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();

    public IVRResponseBuilder withSid(String sid) {
        this.sid = sid;
        return this;
    }

    public IVRResponseBuilder addPlayText(String playText) {
        this.playTexts.add(playText);
        return this;
    }

    public IVRResponseBuilder addPlayAudio(String playAudio) {
        this.playAudios.add(playAudio);
        return this;
    }

    public IVRResponseBuilder withCollectDtmf(CollectDtmf collectDtmf) {
        this.collectDtmf = collectDtmf;
        return this;
    }

    public IVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    public Response create() {
        Response response = new Response();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);
        for (String playText : playTexts) response.addPlayText(playText);
        for (String playAudio : playAudios) response.addPlayAudio(playAudio);
        if (collectDtmf != null) response.addCollectDtmf(collectDtmf);
        if (isHangUp) response.addHangup();
        return response;
    }
}
