package org.motechproject.tama.listener;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.service.TamaSchedulerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.EventKeys.EXTERNAL_ID_KEY;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * When : Time to make an outbox call
 * And  : patient has outbox messages
 */
public class OutboxCallListenerTest {

    private OutboxCallListener outboxCallListener;

    @Mock
    private VoiceOutboxService voiceOutboxService;

    @Mock
    private KookooCallServiceImpl kookooCallServiceImpl;

    @Mock
    private AllPatients allPatients;

    private MotechEvent motechEvent;
    @Mock
    private TamaSchedulerService tamaSchedulerService;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        Properties properties = new Properties();
        properties.put("application.url", "http://tama.com");
        outboxCallListener = new OutboxCallListener(voiceOutboxService, kookooCallServiceImpl, allPatients, properties, tamaSchedulerService);
        setUpEventWithExternalId();
        setUpPatientAsActive();
        setUpOutboxWithAtLeastOneUnreadMessage();
    }

    public void setUpEventWithExternalId() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EXTERNAL_ID_KEY, "patientId");
        motechEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, parameters);
    }

    public void setUpPatientAsActive() {
        patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("0000000000").withPatientId("111").build();
        patient.activate();
        when(allPatients.get(anyString())).thenReturn(patient);
    }

    public void setUpOutboxWithAtLeastOneUnreadMessage() {
        when(voiceOutboxService.getNumberPendingMessages("patientId")).thenReturn(1);
    }

    @Test
    public void shouldMakeACall() {
        ArgumentCaptor<CallRequest> callRequestCaptor = ArgumentCaptor.forClass(CallRequest.class);
        outboxCallListener.handleOutBoxCall(motechEvent);
        verify(kookooCallServiceImpl).initiateCall(callRequestCaptor.capture());
        verify(tamaSchedulerService).scheduleRepeatingJobForOutBoxCall(patient);
    }
}
