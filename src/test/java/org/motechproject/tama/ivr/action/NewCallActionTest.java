package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.Patients;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewCallActionTest {
    private IVRAction action;
    @Mock
    private Patients patients;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private HttpSession httpSession;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        action = new NewCallAction(patients, messages);
    }

    @Test
    public void shouldInitiateASessionWithRequiredAttributesForANewCall() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9898982323", IVR.Event.NEW_CALL.getValue(), "Data");
        when(httpRequest.getSession()).thenReturn(httpSession);

        action.handle(ivrRequest, httpRequest, httpResponse);

        verify(httpSession).setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        verify(httpSession).setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        verify(httpSession).setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
    }

    @Test
    public void shouldSendThePinRequestXMLResponse() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9898982323", IVR.Event.NEW_CALL.getValue(), "Data");
        when(httpRequest.getSession()).thenReturn(httpSession);
        when(messages.get(IVRMessage.Key.TAMA_IVR_ASK_FOR_PIN)).thenReturn("Please enter key");

        String response = action.handle(ivrRequest, httpRequest, httpResponse);
        assertEquals("<response sid=\"unique-call-id\"><collectdtmf><playtext>Please enter key</playtext></collectdtmf></response>", StringUtils.replace(response, "\n", ""));
    }
}
