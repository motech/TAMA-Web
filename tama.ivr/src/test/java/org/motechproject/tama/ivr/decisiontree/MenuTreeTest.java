package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.domain.CallState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MenuTreeTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpSession httpSession;

    public Map<String, Transition> transitions;

    private MenuTree menuTree;


    @Before
    public void setUp() {
        initMocks(this);
        menuTree = new MenuTree(new TAMATreeRegistry());
        setUpContext();
    }

    private void setUpContext() {
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
        transitions = menuTree.createRootNode().getTransitions();
    }

    @Test
    public void shouldTransitionToPullMessagesDTMFInputIs3() {
        assertCallStateTransitionForKeyPress("3", transitions, CallState.PULL_MESSAGES_TREE);
    }

    @Test
    public void shouldTransitionToSymptomsTreeWhenDTMFInputIs2() {
        assertCallStateTransitionForKeyPress("2", transitions, CallState.SYMPTOM_REPORTING);
    }

    private void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState) {
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }

        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }
}
