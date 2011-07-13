package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;

public class IVRResponseBuilder {
    private String sid;
    private String playText;
    private CollectDtmf collectDtmf;
    private boolean isHangUp;

    public IVRResponseBuilder withSid(String sid) {
        this.sid = sid;
        return this;
    }

    public IVRResponseBuilder withPlayText(String playText) {
        this.playText = playText;
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
        if (StringUtils.isNotBlank(playText)) response.addPlayText(playText);
        if (collectDtmf != null) response.addCollectDtmf(collectDtmf);
        if (isHangUp) response.addHangup();
        return response;
    }
}
