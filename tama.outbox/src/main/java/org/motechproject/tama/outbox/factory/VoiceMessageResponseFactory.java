package org.motechproject.tama.outbox.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class VoiceMessageResponseFactory {

    private Set<OutboxMessageBuilder> outboxMessageBuilders;

    @Autowired
    public VoiceMessageResponseFactory(Set<OutboxMessageBuilder> outboxMessageBuilders) {
        this.outboxMessageBuilders = outboxMessageBuilders;
    }

    public void voiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        for (OutboxMessageBuilder outboxMessageBuilder : outboxMessageBuilders) {
            if (outboxMessageBuilder.canHandle(outboundVoiceMessage)) {
                outboxMessageBuilder.buildVoiceMessageResponse(kooKooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            }
        }
    }
}
