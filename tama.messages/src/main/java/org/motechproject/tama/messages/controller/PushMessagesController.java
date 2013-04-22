package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.service.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushMessagesController implements PatientMessagesController {

    private Messages messages;

    @Autowired
    public PushMessagesController(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean markAsReadAndContinue(KooKooIVRContext kooKooIVRContext) {
        PlayedMessage playedMessage = new PlayedMessage(kooKooIVRContext);
        if (playedMessage.exists()) {
            new TAMAIVRContextFactory().create(kooKooIVRContext).setMessagesPushed(true);
            messages.markAsRead(Method.PUSH, kooKooIVRContext, playedMessage);
            return false;
        }
        return true;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kooKooIVRContext);
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().language(kooKooIVRContext.preferredLanguage()).withSid(context.callId());
        return buildResponse(kooKooIVRContext, response);
    }

    private KookooIVRResponseBuilder buildResponse(KooKooIVRContext kooKooIVRContext, KookooIVRResponseBuilder response) {
        List<String> audios = messages.nextMessage(Method.PUSH, kooKooIVRContext, TAMAMessageType.PUSHED_MESSAGE).getPlayAudios();
        audios.add(TamaIVRMessage.END_OF_MESSAGE);
        return response.withPlayAudios(audios.toArray(new String[audios.size()]));
    }
}


