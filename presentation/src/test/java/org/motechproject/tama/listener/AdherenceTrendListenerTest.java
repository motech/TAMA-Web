package org.motechproject.tama.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.service.DailyReminderAdherenceTrendService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendListenerTest {

    AdherenceTrendListener adherenceTrendListener;

    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Before
    public void setUp() {
        initMocks(this);
        adherenceTrendListener = new AdherenceTrendListener(outboxService, dailyReminderAdherenceTrendService);
    }

    @Test
    public void shouldCreateVoiceMessage() {
        final String patientId = "patientId";
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patientId)
                .withExternalId(patientId)
                .payload();
        final MotechEvent motechEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        adherenceTrendListener.handleWeeklyAdherenceTrendToOutboxEvent(motechEvent);
        verify(outboxService).addMessage(Matchers.<OutboundVoiceMessage>any());
        verify(dailyReminderAdherenceTrendService).raiseAdherenceFallingAlert(patientId);
    }
}
