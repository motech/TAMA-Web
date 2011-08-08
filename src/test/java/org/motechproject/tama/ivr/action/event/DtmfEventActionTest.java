package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.AuthenticateAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DtmfEventActionTest extends BaseActionTest {
    private DtmfEventAction eventAction;
    @Mock
    private AuthenticateAction authenticateAction;

    @Before
    public void setUp() {
        super.setUp();
        eventAction = new DtmfEventAction(authenticateAction, null);
    }

    @Test
    public void shouldDelegateToAuthenticateActionIfCollectPin() {
        IVRRequest ivrRequest = new IVRRequest();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_PIN);
        when(authenticateAction.handle(ivrRequest, request, response)).thenReturn("OK");

        String handle = eventAction.handle(ivrRequest, request, response);

        assertEquals("OK", handle);
        verify(authenticateAction).handle(ivrRequest, request, response);
    }
}
