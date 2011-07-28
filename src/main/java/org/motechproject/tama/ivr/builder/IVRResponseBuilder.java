package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;

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

    public IVRResponseBuilder addPlayText(String... playTexts) {
        for (String playText : playTexts)
            this.playTexts.add(playText);
        return this;
    }

    public IVRResponseBuilder addPlayAudio(String... playAudios) {
        for (String playAudio : playAudios)
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

    public IVRResponseBuilder withPreviousDosageReminder(IVRRequest ivrRequest, PillReminderService service, IVRMessage messages) {
        return new PreviousDosageBuilder(ivrRequest, service, messages).build(this);
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
