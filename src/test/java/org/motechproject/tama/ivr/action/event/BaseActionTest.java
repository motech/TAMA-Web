package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BaseActionTest {
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        mockIVRMessage();
    }

    protected void mockIVRMessage() {
        // all IVRMessages must be mocked to achieve correct failing tests
        when(messages.get(IVRMessage.SIGNATURE_MUSIC_URL)).thenReturn("http://music");
        when(messages.get(IVRMessage.WELCOME_MSG)).thenReturn("welcome");
    }
}
