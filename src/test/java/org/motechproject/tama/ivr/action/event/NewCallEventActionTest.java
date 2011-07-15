package org.motechproject.tama.ivr.action.event;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.IVRAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewCallEventActionTest extends BaseActionTest {
    private IVRAction action;

    @Before
    public void setUp() {
        initMocks(this);
        action = new NewCallEventAction(messages);
    }

    @Test
    public void shouldInitiateASessionWithRequiredAttributesForANewCall() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9898982323", IVR.Event.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);

        action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        verify(session).setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        verify(session).setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
    }

    @Test
    public void shouldSendThePinRequestXMLResponse() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9898982323", IVR.Event.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);
        when(messages.get(IVR.MessageKey.TAMA_IVR_ASK_FOR_PIN)).thenReturn("Please enter key");

        String handle = action.handle(ivrRequest, request, response);
        assertEquals("<response sid=\"unique-call-id\"><collectdtmf><playtext>Please enter key</playtext></collectdtmf></response>", StringUtils.replace(handle, "\n", ""));
    }

}
