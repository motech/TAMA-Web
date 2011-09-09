package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.IVRRequest;

import static org.mockito.Mockito.*;

public class DisconnectEventActionTest extends BaseActionTest {

    private DisconnectEventAction action;

    @Before
    public void setUp(){
        super.setUp();
        action = new DisconnectEventAction();
    }

    @Test
    public void shouldCloseIVRSession() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);

        action.postHandle(ivrRequest, request, response);

        verify(session).invalidate();
    }
}
