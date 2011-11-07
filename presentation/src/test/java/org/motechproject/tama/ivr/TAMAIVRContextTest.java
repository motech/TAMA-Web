package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class TAMAIVRContextTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private KookooRequest kookooRequest;
    @Mock
    private Cookies cookies;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void numberOfAttemptsShouldBeInitiatized() {
        HttpSession session = mock(HttpSession.class);
        String callerId = "123";

        when(kookooRequest.getCid()).thenReturn(callerId);
        when(request.getSession()).thenReturn(session);

        TAMAIVRContext tamaivrContext = new TAMAIVRContext(kookooRequest, request, cookies);
        tamaivrContext.initialize();

        verify(session).setAttribute(TAMAIVRContext.CALLER_ID, callerId);
        verify(session).setAttribute(TAMAIVRContext.NUMBER_OF_ATTEMPTS, "0");
    }
}
