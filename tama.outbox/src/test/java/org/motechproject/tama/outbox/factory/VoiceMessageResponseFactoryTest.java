package org.motechproject.tama.outbox.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.outbox.OutboxContextForTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.repository.AllPatients;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VoiceMessageResponseFactoryTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private OutboxMessageFactory dailyOutboxMessageFactory;
    @Mock
    private OutboxMessageFactory weeklyOutboxMessageFactory;

    private OutboxContextForTest outboxContext;
    private VoiceMessageResponseFactory voiceMessageResponseFactory;

    @Before
    public void setup() {
        initMocks(this);
        outboxContext = new OutboxContextForTest().partyId("externalId");
        voiceMessageResponseFactory = new VoiceMessageResponseFactory(allPatients);
        voiceMessageResponseFactory.registerOutboxFactory(CallPreference.DailyPillReminder, dailyOutboxMessageFactory);
        voiceMessageResponseFactory.registerOutboxFactory(CallPreference.FourDayRecall, weeklyOutboxMessageFactory);
    }

    @Test
    public void voiceMessageResponse_ForDailyPillReminder() {
        when(allPatients.get("externalId")).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());

        voiceMessageResponseFactory.voiceMessageResponse(null, outboxContext, null, null);
        verify(dailyOutboxMessageFactory).buildVoiceMessageResponse(null, outboxContext, null, null);
    }

    @Test
    public void voiceMessageResponse_ForFourDayRecall() {
        when(allPatients.get("externalId")).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build());

        voiceMessageResponseFactory.voiceMessageResponse(null, outboxContext, null, null);
        verify(weeklyOutboxMessageFactory).buildVoiceMessageResponse(null, outboxContext, null, null);
    }
}
