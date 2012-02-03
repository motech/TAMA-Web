package org.motechproject.tama.dailypillreminder.outbox;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.CallPreference;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DailyPillReminderOutboxHandlerTest {
    @Mock
    private OutboxService outboxService;
    @Mock
    private OutboxCallListener outboxCallListener;

    private DailyPillReminderOutboxHandler dailyPillReminderOutboxHandler;

    @Before
    public void setUp() {
        initMocks(this);
        dailyPillReminderOutboxHandler = new DailyPillReminderOutboxHandler(outboxService, outboxCallListener);
    }

    @Test
    public void shouldForwardToOutboxService() {
        final MotechEvent motechEvent = new MotechEvent("dummy");
        dailyPillReminderOutboxHandler.handle(motechEvent);
        verify(outboxService).call(motechEvent);
        verify(outboxCallListener).register(CallPreference.DailyPillReminder, dailyPillReminderOutboxHandler);
    }
}
