package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullMessagesTreeTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpSession httpSession;

    private PullMessagesTree pullMessagesTree;

    @Before
    public void setup() {
        initMocks(this);
        pullMessagesTree = new PullMessagesTree(new TAMATreeRegistry());
        setUpContext();
    }

    private void setUpContext() {
        Cookies cookies = mock(Cookies.class);
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldRepeatTheMainMenuOnPressOf9() {
        assertCallStateTransitionForKeyPress("9", pullMessagesTree.createRootNode().getTransitions(), CallState.AUTHENTICATED);
    }

    private void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState) {
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }
        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }
}
