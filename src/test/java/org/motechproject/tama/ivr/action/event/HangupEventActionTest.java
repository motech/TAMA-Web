package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.IVRRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HangupEventActionTest extends BaseActionTest {
    private HangupEventAction action;

    @Before
    public void setUp() {
        super.setUp();
        action = new HangupEventAction();
    }

    @Test
    public void shouldCloseIVRSession() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);

        action.handle(ivrRequest, request, response);

        verify(session).invalidate();
    }
}
