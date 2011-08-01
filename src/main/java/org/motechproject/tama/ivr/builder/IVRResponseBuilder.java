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
    private boolean collectDtmf;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();

    public IVRResponseBuilder(String sid) {
        this.sid = sid;
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

    public IVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    public IVRResponseBuilder withPreviousDosageReminder(IVRRequest ivrRequest, PillReminderService service, IVRMessage messages) {
        return new PreviousDosageBuilder(ivrRequest, service, messages).build(this);
    }

    public Response create(IVRMessage ivrMessage) {
        Response response = new Response();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);

        if (collectDtmf) {
            CollectDtmf collectDtmf = new CollectDtmf();
            for (String playText : playTexts) collectDtmf.addPlayText(ivrMessage.get(playText));
            for (String playAudio : playAudios) collectDtmf.addPlayAudio(ivrMessage.getWav(playAudio));

            response.addCollectDtmf(collectDtmf);
        } else {
            for (String playText : playTexts) response.addPlayText(ivrMessage.get(playText));
            for (String playAudio : playAudios) response.addPlayAudio(ivrMessage.getWav(playAudio));
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
