package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderCallTest {
    private PillReminderCall pillReminderCall;
    @Mock
    private Patients patients;
    @Mock
    private CallService callService;

    private String PHONE_NUMBER = "1234567890";
    private static final String PATIENT_DOC_ID = "P_1";
    private static final String REGIMEN_ID = "R_1";
    private static final String DOSAGE_ID = "D_1";

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = new PillReminderCall(callService, patients);
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(patients.get(PATIENT_DOC_ID)).thenReturn(null);

        pillReminderCall.execute(PATIENT_DOC_ID, REGIMEN_ID, DOSAGE_ID);

        verify(callService, never()).dial(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotMakeACallForInActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(true);
        when(patients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, REGIMEN_ID, DOSAGE_ID);

        verify(callService, never()).dial(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldMakeACallForActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(false);
        when(patient.getIVRMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(patients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, REGIMEN_ID, DOSAGE_ID);

        verify(callService).dial(eq(PHONE_NUMBER), argThat(new IntermediateCallParametersMatcher()));
    }

    @Test
    public void shouldMakeLastCallForActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(false);
        when(patient.getIVRMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(patients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.executeLastCall(PATIENT_DOC_ID, REGIMEN_ID, DOSAGE_ID);

        verify(callService).dial(eq(PHONE_NUMBER), argThat(new LastCallParametersMatcher()));
    }

    public class IntermediateCallParametersMatcher extends ArgumentMatcher<Map> {
        @Override
        public boolean matches(Object o) {
            Map map = (Map) o;
            return map.get(PillReminderCall.DOSAGE_ID).equals(DOSAGE_ID)
                   && map.get(PillReminderCall.REGIMEN_ID).equals(REGIMEN_ID)
                   && map.get(PillReminderCall.LAST_CALL).equals("false");
        }
    }

    public class LastCallParametersMatcher extends ArgumentMatcher<Map> {
        @Override
        public boolean matches(Object o) {
            Map map = (Map) o;
            return map.get(PillReminderCall.DOSAGE_ID).equals(DOSAGE_ID)
                   && map.get(PillReminderCall.REGIMEN_ID).equals(REGIMEN_ID)
                   && map.get(PillReminderCall.LAST_CALL).equals("true");
        }
    }

}
