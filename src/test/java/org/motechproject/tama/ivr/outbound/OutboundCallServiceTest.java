package org.motechproject.tama.ivr.outbound;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;

import java.io.IOException;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboundCallServiceTest {
    @Mock
    private Patients patients;
    @Mock
    private HttpClient client;
    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);
        properties = new Properties();
        properties.setProperty(OutboundCallService.KOOKOO_OUTBOUND_URL, "http://kookoo/outbound.php");
        properties.setProperty(OutboundCallService.KOOKOO_API_KEY, "KKbedce53758c2e0b0e9eed7191ec2a466");
        properties.setProperty(OutboundCallService.APPLICATION_URL, "http://localhost/tama/ivr/reply");
    }

    @Test
    public void shouldMakeAnOutgoingCallGivenAPatientId() throws IOException {
        String patientId = "1234";
        final String mobileNumber = "9876543210";
        Patient patient = PatientBuilder.startRecording().withMobileNumber(mobileNumber).withStatus(Patient.Status.Active).build();
        when(patients.get(patientId)).thenReturn(patient);

        OutboundCallService outboundCallService = new OutboundCallService(patients, client, properties);
        outboundCallService.call(patientId);

        verify(patients).get(patientId);
        verify(client).executeMethod(argThat(new GetMethodMatcher()));
    }

    @Test
    public void shouldNotMakeAnOutgoingCallForAnInactivePatient() throws IOException {
        String patientId = "1234";
        final String mobileNumber = "9876543210";
        Patient patient = PatientBuilder.startRecording().withMobileNumber(mobileNumber).withStatus(Patient.Status.Inactive).build();
        when(patients.get(patientId)).thenReturn(patient);

        OutboundCallService outboundCallService = new OutboundCallService(patients, client, properties);
        outboundCallService.call(patientId);

        verify(patients).get(patientId);
        verify(client, never()).executeMethod(any(GetMethod.class));
    }

    @Test
    public void shouldNotMakeAnOutgoingCallWhenNoPatientIsFound() throws IOException {
        String patientId = "1234";
        when(patients.get(patientId)).thenReturn(null);

        OutboundCallService outboundCallService = new OutboundCallService(patients, client, properties);
        outboundCallService.call(patientId);

        verify(patients).get(patientId);
        verify(client, never()).executeMethod(any(GetMethod.class));
    }

    public class GetMethodMatcher extends ArgumentMatcher<GetMethod> {
        @Override
        public boolean matches(Object o) {
            GetMethod getMethod = (GetMethod) o;
            try {
                return getMethod.getURI().getURI().equals("http://kookoo/outbound.php?api_key=KKbedce53758c2e0b0e9eed7191ec2a466&url=http://localhost/tama/ivr/reply&phone_no=09876543210");
            } catch (URIException e) {
                return false;
            }
        }
    }
}
