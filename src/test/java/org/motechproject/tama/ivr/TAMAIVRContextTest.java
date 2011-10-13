package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;

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
        String callerId = "123";
        when(kookooRequest.getCid()).thenReturn(callerId);
        TAMAIVRContext tamaivrContext = new TAMAIVRContext(kookooRequest, request, cookies);
        tamaivrContext.initialize();
        verify(cookies).add(TAMAIVRContext.CALLER_ID, callerId);
        verify(cookies).add(TAMAIVRContext.NUMBER_OF_ATTEMPTS, "0");
        verify(cookies).add(TAMAIVRContext.NUMBER_OF_TIMES_REMINDER_SENT, "0");
    }
}
