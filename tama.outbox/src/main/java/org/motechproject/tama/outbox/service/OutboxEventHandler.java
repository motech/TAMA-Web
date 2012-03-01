package org.motechproject.tama.outbox.service;

import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.springframework.stereotype.Component;

@Component
public interface OutboxEventHandler {
    void onCreate(OutboundVoiceMessage message);
}
