package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.repository.AllPatients;

import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderCallTest {
    private PillReminderCall pillReminderCall;
    @Mock
    private AllPatients allPatients;
    @Mock
    private IVRService callService;
    @Mock
    private EventService eventService;
    @Mock
    private CallLogService callLogService;

    private String PATIENT_DOC_ID = "P_1";
    private String DOSAGE_ID = "D_1";
    private int TOTAL_TIMES_TO_SEND = 2;
    private int TIMES_SENT = 0;
    private int RETRY_INTERVAL = 5;

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = Mockito.spy(new PillReminderCall(callService, allPatients , new Properties()));
        Mockito.doReturn("").when(pillReminderCall).getApplicationUrl();
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(null);
        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);
        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldNotMakeACallForInActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.allowAdherenceCalls()).thenReturn(false);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldMakeACallForActivePatient() {
        String PHONE_NUMBER = "1234567890";
        Patient patient = mock(Patient.class);
        when(patient.allowAdherenceCalls()).thenReturn(true);
        when(patient.getMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        ArgumentCaptor<CallRequest> callRequestArgumentCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(callService).initiateCall(callRequestArgumentCaptor.capture());
        Map<String,String> payload = callRequestArgumentCaptor.getValue().getPayload();
        assertEquals(String.valueOf(TOTAL_TIMES_TO_SEND), payload.get(PillReminderCall.TOTAL_TIMES_TO_SEND));
        assertEquals(String.valueOf(TIMES_SENT), payload.get(PillReminderCall.TIMES_SENT));
        assertEquals(String.valueOf(RETRY_INTERVAL), payload.get(PillReminderCall.RETRY_INTERVAL));
    }
}