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
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.service.DailyReminderAdherenceTrendService;
import org.powermock.api.mockito.PowerMockito;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendListenerTest {

    AdherenceTrendListener adherenceTrendListener;

    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    @Mock
    private AllPatients allPatients;

    @Before
    public void setUp() {
        initMocks(this);
        adherenceTrendListener = new AdherenceTrendListener(outboxService, dailyReminderAdherenceTrendService, allPatients);
    }

    @Test
    public void shouldCreateVoiceMessage() {
        final String patientId = "patientId";
        PowerMockito.when(allPatients.get(patientId)).thenReturn(PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Active).build());
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patientId)
                .withExternalId(patientId)
                .payload();
        final MotechEvent motechEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        adherenceTrendListener.handleWeeklyAdherenceTrendToOutboxEvent(motechEvent);
        verify(outboxService).addMessage(Matchers.<OutboundVoiceMessage>any());
        verify(dailyReminderAdherenceTrendService).raiseAdherenceFallingAlert(patientId);
    }

    @Test
    public void shouldNotCreateVoiceMessageIfPatientIsNotActive() {
        final String patientId = "patientId";
        final Patient patient = new Patient();
        patient.setStatus(Patient.Status.Suspended);
        when(allPatients.get(patientId)).thenReturn(patient);
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patientId)
                .withExternalId(patientId)
                .payload();

        adherenceTrendListener.handleWeeklyAdherenceTrendToOutboxEvent(new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams));
        verifyZeroInteractions(outboxService);
        verifyZeroInteractions(dailyReminderAdherenceTrendService);
    }
}
