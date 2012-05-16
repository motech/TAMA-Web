package org.motechproject.tama.outbox.service;

import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
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
        LocalDate today = DateUtil.today();
        OutboxMessageLog log = new OutboxMessageLog(message.getExternalId(), message.getId(), DateUtil.newDateTime(today), message.getVoiceMessageType().getVoiceMessageTypeName());
        allOutboxLogs.add(log);
    }

    @Override
    public void onPlayed(String patientDocId, KookooIVRResponseBuilder ivrResponseBuilder, String messageId) {
        OutboxMessageLog messageLog = allOutboxLogs.find(patientDocId, messageId);
        messageLog.playedOn(DateUtil.now(), ivrResponseBuilder.getPlayAudios());
        allOutboxLogs.update(messageLog);
    }
}
