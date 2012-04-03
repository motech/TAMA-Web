package org.motechproject.tama.outbox.integration;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class DummyBuilder implements OutboxMessageBuilder {
    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        return false;
    }

    @Override
    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
    }
}
