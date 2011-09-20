package org.motechproject.tama.ivr;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;

public class IVRSessionTest {
    private IVRSession ivrSession;
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
        ivrSession = new IVRSession(session);
    }

    @Test
    public void shouldGetCallState() {
        IVRCallState expectedState = IVRCallState.AUTH_SUCCESS;
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(expectedState);

        IVRCallState state = ivrSession.getState();

        verify(session).getAttribute(IVRCallAttribute.CALL_STATE);
        assertEquals(expectedState, state);
    }

    @Test
    public void shouldSetCallState() {
        IVRCallState state = IVRCallState.AUTH_SUCCESS;
        ivrSession.setState(state);
        verify(session).setAttribute(IVRCallAttribute.CALL_STATE, state);
    }

    @Test
    public void shouldGetAttribute() {
        String key = "key";
        String value = "value";
        when(session.getAttribute(key)).thenReturn(value);
        assertEquals(value, ivrSession.get(key));
    }

    @Test
    public void shouldSetAttribute() {
        String key = "key";
        String value = "value";
        ivrSession.set(key, value);
        verify(session).setAttribute(key, value);
    }

    @Test
    public void shouldRenewSession(){
        ivrSession.renew(request);

        verify(session).invalidate();
        verify(request).getSession();
    }
    @Test 
    public void shouldReturnCallType() {
    	when(session.getAttribute(TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM)).thenReturn("true");
    	Assert.assertTrue(TamaSessionUtil.isSymptomsReportingCall(ivrSession));
    }
}
