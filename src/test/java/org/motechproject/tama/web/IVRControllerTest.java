package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.IVRAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.MockitoAnnotations.initMocks;

public class IVRControllerTest {
    IVRController controller;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new IVRController();
    }

    @Test
    public void shouldDelegateToActionsToHandleTheRequest() {
        IVRAction action = mock(IVRAction.class);
        when(ivrRequest.getAction()).thenReturn(action);
        when(action.handle(ivrRequest, request, response)).thenReturn("reply");

        String reply = controller.reply(ivrRequest, request, response);

        verify(action).handle(ivrRequest, request, response);
        assertEquals("reply", reply);
    }
}
