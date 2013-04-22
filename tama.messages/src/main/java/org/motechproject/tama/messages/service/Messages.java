package org.motechproject.tama.messages.service;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.provider.MessageProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    private MessageProviders messageProviders;
    private HealthTipMessage healthTipMessage;

    @Autowired
    public Messages(MessageProviders messageProviders, HealthTipMessage healthTipMessage) {
        this.messageProviders = messageProviders;
        this.healthTipMessage = healthTipMessage;
    }

    public KookooIVRResponseBuilder nextMessage(KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        return messages(kooKooIVRContext, type);
    }

    public KookooIVRResponseBuilder nextHealthTip(KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        if (healthTipMessage.hasAnyMessage(kooKooIVRContext, type)) {
            return healthTipMessage.getResponse(kooKooIVRContext, type);
        } else {
            return new KookooIVRResponseBuilder().language(kooKooIVRContext.preferredLanguage()).withSid(kooKooIVRContext.callId());
        }
    }

    public void markAsRead(KooKooIVRContext kookooIVRContext, PlayedMessage playedMessage) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kookooIVRContext);
        if (PlayedMessage.Types.HEALTH_TIPS.equals(playedMessage.type())) {
            healthTipMessage.markAsRead(context.patientDocumentId(), playedMessage.id());
        } else {
            messageProviders.markAsRead(context.getTAMAMessageType(), playedMessage.id());
        }
    }

    private KookooIVRResponseBuilder messages(KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (messageProviders.hasAnyMessage(tamaivrContext, type)) {
            return messageProviders.getResponse(tamaivrContext, type);
        } else if (healthTipMessage.hasAnyMessage(kooKooIVRContext, type)) {
            return healthTipMessage.getResponse(kooKooIVRContext, type);
        } else {
            return new KookooIVRResponseBuilder().language(kooKooIVRContext.preferredLanguage()).withSid(kooKooIVRContext.callId());
        }
    }
}
