package org.motechproject.tamacallflow.listener;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.Status;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.EventKeys.EXTERNAL_ID_KEY;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * When : Time to make an outbox call
 * And  : patient has outbox messages
 */
public class OutboxCallListenerTest {

    private OutboxCallListener outboxCallListener;
    private MotechEvent motechEvent;
    private Patient patient;

    @Mock
    private VoiceOutboxService voiceOutboxService;
    @Mock
    private KookooCallServiceImpl kookooCallServiceImpl;
    @Mock
    private AllPatients allPatients;
    @Mock
    private TamaSchedulerService tamaSchedulerService;

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

    @Test
    public void shouldMakeACall() {
        patient.setStatus(Status.Active);
        ArgumentCaptor<CallRequest> callRequestCaptor = ArgumentCaptor.forClass(CallRequest.class);
        outboxCallListener.handleOutBoxCall(motechEvent);
        verify(kookooCallServiceImpl).initiateCall(callRequestCaptor.capture());
        verify(tamaSchedulerService).scheduleRepeatingJobForOutBoxCall(patient);
    }

    @Test
    public void shouldMakeACallEvenWhenPatientIsSuspended() {
        patient.setStatus(Status.Suspended);
        Mockito.when(allPatients.get(EXTERNAL_ID_KEY)).thenReturn(patient);

        ArgumentCaptor<CallRequest> callRequestCaptor = ArgumentCaptor.forClass(CallRequest.class);
        outboxCallListener.handleOutBoxCall(motechEvent);
        verify(kookooCallServiceImpl).initiateCall(callRequestCaptor.capture());
        verify(tamaSchedulerService).scheduleRepeatingJobForOutBoxCall(patient);
    }

    @Test
    public void shouldNotMakeACallEvenWhenPatientIsInactive() {
        patient.setStatus(Status.Loss_To_Follow_Up);
        Mockito.when(allPatients.get(EXTERNAL_ID_KEY)).thenReturn(patient);

        outboxCallListener.handleOutBoxCall(motechEvent);
        verifyZeroInteractions(kookooCallServiceImpl);
        verifyZeroInteractions(tamaSchedulerService);
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

}
