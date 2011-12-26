package org.motechproject.tama.dailypillreminder.listener;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.powermock.api.mockito.PowerMockito;

import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendListenerTest {

    AdherenceTrendListener adherenceTrendListener;

    @Mock
    private OutboxService outboxService;
    @Mock
    DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
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
        PowerMockito.when(allPatients.get(patientId)).thenReturn(PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).build());
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patientId)
                .withExternalId(patientId)
                .payload();
        final MotechEvent motechEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        adherenceTrendListener.handleAdherenceTrendEvent(motechEvent);
        verify(outboxService).addMessage(patientId);
        verify(dailyReminderAdherenceTrendService).raiseAlertIfAdherenceTrendIsFalling(eq(patientId), Matchers.<DateTime>any());
    }

    @Test
    public void shouldNotCreateVoiceMessageIfPatientIsSuspended() {
        final String patientId = "patientId";
        final Patient patient = new Patient();
        patient.setStatus(Status.Suspended);
        when(allPatients.get(patientId)).thenReturn(patient);
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patientId)
                .withExternalId(patientId)
                .payload();

        adherenceTrendListener.handleAdherenceTrendEvent(new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams));
        verifyZeroInteractions(outboxService);
        verifyZeroInteractions(dailyReminderAdherenceTrendService);
    }
}
