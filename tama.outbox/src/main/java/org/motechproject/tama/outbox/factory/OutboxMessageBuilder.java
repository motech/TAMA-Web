package org.motechproject.tama.outbox.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.context.OutboxContext;

public interface OutboxMessageBuilder {
    public boolean canHandle(OutboundVoiceMessage voiceMessage);

    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder);
}
