package org.motechproject.tama.dailypillreminder.outbox;

import org.motechproject.model.MotechEvent;
import org.motechproject.tama.outbox.handler.OutboxHandler;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.CallPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderOutboxHandler implements OutboxHandler {
    private OutboxService outboxService;

    @Autowired
    public DailyPillReminderOutboxHandler(OutboxService outboxService, OutboxCallListener outboxCallListener) {
        this.outboxService = outboxService;
        outboxCallListener.register(CallPreference.DailyPillReminder, this);
    }

    @Override
    public void handle(MotechEvent motechEvent) {
        outboxService.call(motechEvent);
    }
}
