package org.motechproject.tama.outbox.service;

import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.repository.AllOutboxEvents;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventLogger implements OutboxEventHandler {

    private AllOutboxEvents allOutboxEvents;

    @Autowired
    public OutboxEventLogger(AllOutboxEvents allOutboxEvents) {

        this.allOutboxEvents = allOutboxEvents;
    }

    @Override
    public void onCreate(OutboundVoiceMessage message) {
        OutboxMessageLog log = new OutboxMessageLog(message.getId(), DateUtil.now(), OutboxEventType.Created);
        allOutboxEvents.add(log);
    }
}
