package org.motechproject.tama.fourdayrecall.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallIncomingCallTreeTest {
    private FourDayRecallIncomingCallTree fourDayRecallIncomingCallTree;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpSession httpSession;
    public Map<String,Transition> transitions;


    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallIncomingCallTree = new FourDayRecallIncomingCallTree(new TAMATreeRegistry());
        setUpContext();
    }

    private void setUpContext() {
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
        transitions = fourDayRecallIncomingCallTree.createRootNode().getTransitions();
    }

    @Test
    public void shouldTransitionToOutboxTreeWhenDTMFInputIs3() {
        assertCallStateTransitionForKeyPress("3", transitions, CallState.OUTBOX);
    }

    @Test
    public void shouldTransitionToSymptomsTreeWhenDTMFInputIs2(){
        assertCallStateTransitionForKeyPress("2", transitions, CallState.SYMPTOM_REPORTING);
    }

    private void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState){
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }

        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }
}
