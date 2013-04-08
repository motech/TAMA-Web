package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
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
    @Mock
    private Cookies cookies;

    private PullMessagesTree pullMessagesTree;

    @Before
    public void setup() {
        initMocks(this);
        pullMessagesTree = new PullMessagesTree(new TAMATreeRegistry());
        setUpContext();
    }

    private void setUpContext() {
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldPlayMessagesOnPressOf1() {
        assertCallStateTransitionForKeyPress("1", pullMessagesTree.createRootNode().getTransitions(), CallState.PULL_MESSAGES);
    }

    @Test
    public void shouldPlayHealthTipsOnPressOfKeysFrom2Through7() {
        assertMessageTransitionForKeyPress("2", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.FAMILY_AND_CHILDREN);
        assertMessageTransitionForKeyPress("3", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.NUTRITION_AND_LIFESTYLE);
        assertMessageTransitionForKeyPress("4", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.SYMPTOMS);
        assertMessageTransitionForKeyPress("5", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.ADHERENCE_TO_ART);
        assertMessageTransitionForKeyPress("6", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.ART_AND_CD4);
        assertMessageTransitionForKeyPress("7", pullMessagesTree.createRootNode().getTransitions(), TAMAMessageType.LIVING_WITH_HIV);
    }

    @Test
    public void shouldRepeatTheMainMenuOnPressOf9() {
        assertCallStateTransitionForKeyPress("9", pullMessagesTree.createRootNode().getTransitions(), CallState.MAIN_MENU);
    }

    private void assertMessageTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, TAMAMessageType type) {
        assertCallStateTransitionForKeyPress(keyPressed, transitions, CallState.PULL_MESSAGES);
        verify(cookies).add(TAMAIVRContext.MESSAGE_CATEGORY_NAME, type.name());
    }

    private void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState) {
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }
        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }
}
