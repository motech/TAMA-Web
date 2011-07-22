package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.Actions;
import org.motechproject.tama.ivr.action.IVRIncomingAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRControllerTest {
    IVRController controller;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Actions actions;
    @Mock
    private IVRIncomingAction action;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new IVRController(actions);
    }

    @Test
    public void shouldDelegateToActionsToHandleTheRequest() {
        when(ivrRequest.callEvent()).thenReturn(IVR.Event.HANGUP);
        when(actions.findFor(IVR.Event.HANGUP)).thenReturn(action);
        when(action.handle(ivrRequest, request, response)).thenReturn("reply");

        String reply = controller.reply(ivrRequest, request, response);

        verify(action).handle(ivrRequest, request, response);
        assertEquals("reply", reply);
    }
}
