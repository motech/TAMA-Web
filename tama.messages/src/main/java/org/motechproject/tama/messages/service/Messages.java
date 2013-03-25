package org.motechproject.tama.messages.service;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.TAMAMessageTypes;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    private OutboxMessage outboxMessage;
    private HealthTipMessage healthTipMessage;

    @Autowired
    public Messages(OutboxMessage outboxMessage, HealthTipMessage healthTipMessage) {
        this.outboxMessage = outboxMessage;
        this.healthTipMessage = healthTipMessage;
    }

    public KookooIVRResponseBuilder nextMessage(KooKooIVRContext kooKooIVRContext) {
        return messages(kooKooIVRContext);
    }

    public KookooIVRResponseBuilder nextHealthTip(KooKooIVRContext kooKooIVRContext, TAMAMessageTypes type) {
        if (healthTipMessage.hasAnyMessage(kooKooIVRContext, type)) {
            return healthTipMessage.getResponse(kooKooIVRContext, type);
        } else {
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        }
    }

    public void markAsRead(KooKooIVRContext kookooIVRContext, PlayedMessage playedMessage) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kookooIVRContext);
        if (PlayedMessage.Types.HEALTH_TIPS.equals(playedMessage.type())) {
            healthTipMessage.markAsRead(context.patientDocumentId(), playedMessage.id());
        } else {
            outboxMessage.markAsRead(kookooIVRContext);
        }
    }

    private KookooIVRResponseBuilder messages(KooKooIVRContext kooKooIVRContext) {
        if (outboxMessage.hasAnyMessage(kooKooIVRContext)) {
            return outboxMessage.getResponse(kooKooIVRContext);
        } else if (healthTipMessage.hasAnyMessage(kooKooIVRContext, null)) {
            return healthTipMessage.getResponse(kooKooIVRContext, null);
        } else {
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        }
    }
}
