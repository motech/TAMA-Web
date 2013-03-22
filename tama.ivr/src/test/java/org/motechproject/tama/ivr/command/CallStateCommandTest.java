package org.motechproject.tama.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallStateCommandTest {

    @Mock
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private Cookies cookies;

    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        setupContext();
    }

    @Test
    public void updateIVRContextOnExecute() {
        CallStateCommand callStateCommand = new CallStateCommand(CallState.ALL_TREES_COMPLETED, tamaivrContextFactory);
        callStateCommand.execute(kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, context.callState());
    }

    @Test
    public void shouldResetContextWhenRequested() {
        context.callState(CallState.ALL_TREES_COMPLETED);
        CallStateCommand callStateCommand = new CallStateCommand(tamaivrContextFactory);
        callStateCommand.execute(kooKooIVRContext);

        verify(cookies).add(TAMAIVRContext.DO_NOT_PROMPT_FOR_HANG_UP, "true");
        assertEquals(CallState.MAIN_MENU, context.callState());
    }

    private void setupContext() {
        setupKookooContext();
        context = new TAMAIVRContextForTest(kooKooIVRContext);
        context.callState(CallState.AUTHENTICATED);
        when(tamaivrContextFactory.create(kooKooIVRContext)).thenReturn(context);
    }

    private void setupKookooContext() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(kooKooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(session);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }
}
