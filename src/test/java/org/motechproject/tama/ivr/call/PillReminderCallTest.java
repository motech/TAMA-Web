package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.repository.AllPatients;

import java.util.Properties;

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
    private String PHONE_NUMBER = "1234567890";

    private static final String PATIENT_DOC_ID = "P_1";
    private static final String DOSAGE_ID = "D_1";
    private static final int TOTAL_TIMES_TO_SEND = 2;
    private static final int TIMES_SENT = 0;

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = Mockito.spy(new PillReminderCall(callService, allPatients , new Properties()));
        Mockito.doReturn("").when(pillReminderCall).getApplicationUrl();
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(null);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);
        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldNotMakeACallForInActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(true);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);

        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldMakeACallForActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(false);
        when(patient.getIVRMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);

        verify(callService).initiateCall(any(CallRequest.class));
    }

}
