package org.motechproject.tama.outbox.service;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventLogger implements OutboxEventHandler {

    private AllOutboxLogs allOutboxLogs;

    @Autowired
    public OutboxEventLogger(AllOutboxLogs allOutboxLogs) {
        this.allOutboxLogs = allOutboxLogs;
    }

    @Override
    public void onCreate(OutboundVoiceMessage message) {
        OutboxMessageLog log = new OutboxMessageLog(message.getId(), DateUtil.now(), OutboxEventType.Created);
        allOutboxLogs.add(log);
    }

    @Override
    public void onPlayed(KookooIVRResponseBuilder ivrResponseBuilder, String messageId) {
        final OutboxMessageLog outboxMessageLog = new OutboxMessageLog(messageId,
                DateUtil.now(),
                OutboxEventType.Played,
                ivrResponseBuilder.getPlayAudios());

        allOutboxLogs.add(outboxMessageLog);
    }
}
