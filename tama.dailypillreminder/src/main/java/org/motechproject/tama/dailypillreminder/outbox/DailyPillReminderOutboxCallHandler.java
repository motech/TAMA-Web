package org.motechproject.tama.dailypillreminder.outbox;

import org.motechproject.model.MotechEvent;
import org.motechproject.tama.outbox.handler.OutboxCallHandler;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.CallPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderOutboxCallHandler implements OutboxCallHandler {
    private OutboxService outboxService;

    @Autowired
    public DailyPillReminderOutboxCallHandler(OutboxService outboxService, OutboxCallListener outboxCallListener) {
        this.outboxService = outboxService;
        outboxCallListener.register(CallPreference.DailyPillReminder, this);
    }

    @Override
    public void handle(MotechEvent motechEvent) {
        outboxService.call(motechEvent);
    }
}
