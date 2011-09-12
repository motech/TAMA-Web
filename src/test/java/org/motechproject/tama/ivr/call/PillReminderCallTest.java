package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderCallTest {
    private PillReminderCall pillReminderCall;
    @Mock
    private AllPatients allPatients;
    @Mock
    private CallService callService;
    @Mock 
    private EventService eventService;

    private String PHONE_NUMBER = "1234567890";
    private static final String PATIENT_DOC_ID = "P_1";
    private static final String DOSAGE_ID = "D_1";
    private static final int TOTAL_TIMES_TO_SEND = 2;
    private static final int TIMES_SENT = 0;

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = new PillReminderCall(callService, eventService, allPatients);
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(null);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);

        verify(callService, never()).dial(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotMakeACallForInActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(true);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);

        verify(callService, never()).dial(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldMakeACallForActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(false);
        when(patient.getIVRMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID, TIMES_SENT, TOTAL_TIMES_TO_SEND);

        verify(callService).dial(eq(PHONE_NUMBER), argThat(new IntermediateCallParametersMatcher()));
    }

    public class IntermediateCallParametersMatcher extends ArgumentMatcher<Map> {
        @Override
        public boolean matches(Object o) {
            Map map = (Map) o;
            return map.get(PillReminderCall.DOSAGE_ID).equals(DOSAGE_ID);
        }
    }

    public class LastCallParametersMatcher extends ArgumentMatcher<Map> {
        @Override
        public boolean matches(Object o) {
            Map map = (Map) o;
            return map.get(PillReminderCall.DOSAGE_ID).equals(DOSAGE_ID);
        }
    }
}
