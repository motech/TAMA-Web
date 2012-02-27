package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRCallTest {

    @Mock
    private IVRService ivrService;
    private Properties properties;
    private IVRCall ivrCall;

    @Before
    public void setUp() {
        initMocks(this);
        properties = new Properties();
        properties.put(IVRCall.APPLICATION_URL, "tama");
        ivrCall = new IVRCall(ivrService, properties);
    }

    @Test
    public void shouldAddPatientDocId_ToCallRequestParams() {
        final String patientDocId = "patientDocId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocId).build();

        ivrCall.makeCall(patient, "outbox", new HashMap<String, String>());

        ArgumentCaptor<CallRequest> callRequestArgumentCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(ivrService).initiateCall(callRequestArgumentCaptor.capture());

        final CallRequest callRequest = callRequestArgumentCaptor.getValue();
        assertEquals(2, callRequest.getPayload().size());
        assertEquals("tama", callRequest.getCallBackUrl());
        assertEquals(patientDocId, callRequest.getPayload().get(IVRService.EXTERNAL_ID));
        assertEquals("outbox", callRequest.getPayload().get(IVRService.CALL_TYPE));
    }
}
