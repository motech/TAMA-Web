package org.motechproject.tama.messages.controller;

import org.apache.commons.lang.StringUtils;
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

@Component
public class PullMessagesController implements PatientMessagesController {

    private Messages messages;

    @Autowired
    public PullMessagesController(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean markAsReadAndContinue(KooKooIVRContext kooKooIVRContext) {
        PlayedMessage playedMessage = new PlayedMessage(kooKooIVRContext);
        if (playedMessage.exists()) {
            messages.markAsRead(Method.PULL, kooKooIVRContext, playedMessage);
        }
        return true;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (StringUtils.equals("9", tamaivrContext.dtmfInput())) {
            return new KookooIVRResponseBuilder().language(kooKooIVRContext.preferredLanguage()).withSid(kooKooIVRContext.callId());
        } else {
            return sanitizeResponse(Method.PULL, tamaivrContext);
        }
    }

    private KookooIVRResponseBuilder sanitizeResponse(Method method, TAMAIVRContext tamaivrContext) {
        KookooIVRResponseBuilder response = nextMessage(method, tamaivrContext);
        if (response.isNotEmpty()) {
            response.withPlayAudios(TamaIVRMessage.END_OF_MESSAGE,TamaIVRMessage.PRESS_9_FOR_MAIN_MENU);
            return response.collectDtmfLength(1);
        } else {
            return response;
        }
    }

    private KookooIVRResponseBuilder nextMessage(Method method, TAMAIVRContext tamaIVRContext) {
        TAMAMessageType type = tamaIVRContext.getMessagesCategory();
        if (TAMAMessageType.ALL_MESSAGES.equals(type) || TAMAMessageType.ADHERENCE_TO_ART.equals(type)) {
            return messages.nextMessage(method, tamaIVRContext.getKooKooIVRContext(), type);
        } else {
            return messages.nextHealthTip(tamaIVRContext.getKooKooIVRContext(), type);
        }
    }
}
