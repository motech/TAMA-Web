package org.motechproject.tama.outbox.listener;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.outbox.handler.OutboxHandler;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxCallListenerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private OutboxHandler dailyPillReminderOutboxHandler;
    private OutboxCallListener outboxCallListener;

    @Before
    public void setUp() {
        initMocks(this);
        outboxCallListener = new OutboxCallListener(allPatients);
    }

    @Test
    public void shouldForwardToTheRespectiveHandler_BasedOnCallPreference() {
        outboxCallListener.register(CallPreference.DailyPillReminder, dailyPillReminderOutboxHandler);
        final String patientId = "patientId";
        final MotechEvent motechEvent = new MotechEvent("foo", new HashMap<String, Object>() {{put(OutboxSchedulerService.EXTERNAL_ID_KEY, patientId);}});

        when(allPatients.get(patientId)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        outboxCallListener.handle(motechEvent);
        verify(dailyPillReminderOutboxHandler).handle(motechEvent);
    }
}
