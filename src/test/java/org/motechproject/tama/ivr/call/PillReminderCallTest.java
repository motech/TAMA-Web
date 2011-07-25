package org.motechproject.tama.ivr.call;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class PillReminderCallTest {

    @Mock
    private Patients patients;
    @Mock
    private CallService callService;

    private PillReminderCall pillReminderCall;

    private static final String PATIENT_DOC_ID = "123456";
    private String PHONE_NUMBER = "1234567890";
    private static final String DOSAGE_ID = "123345";

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = new PillReminderCall(callService, patients);
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(patients.get(PATIENT_DOC_ID)).thenReturn(null);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID);

        verify(callService, never()).call(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotMakeACallForInActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(true);
        when(patients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID);

        verify(callService, never()).call(anyString(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotMakeACallForActivePatient() {
        Patient patient = mock(Patient.class);
        when(patient.isNotActive()).thenReturn(false);
        when(patient.getIVRMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(patients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, DOSAGE_ID);

        verify(callService).call(eq(PHONE_NUMBER), argThat(new MapMatcher()));
    }

    public class MapMatcher extends ArgumentMatcher<Map> {
        @Override
        public boolean matches(Object o) {
            Map map = (Map) o;
            return map.get(PillReminderCall.DOSAGE_ID).equals(DOSAGE_ID);
        }
    }

}
