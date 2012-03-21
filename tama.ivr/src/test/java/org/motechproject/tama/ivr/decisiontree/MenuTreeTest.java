package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.domain.CallState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MenuTreeTest {

    private MenuTree menuTree;
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
        menuTree = new MenuTree(new TAMATreeRegistry());
        setUpContext();
    }

    private void setUpContext() {
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
        transitions = menuTree.createRootNode().getTransitions();
    }

    @Test
    public void shouldTransitionToOutboxTreeWhenDTMFInputIs3() {
        assertCallStateTransitionForKeyPress("3", transitions, CallState.OUTBOX);
    }

    @Test
    public void shouldTransitionToSymptomsTreeWhenDTMFInputIs2(){
        assertCallStateTransitionForKeyPress("2", transitions, CallState.SYMPTOM_REPORTING);
        assertAudioFilePresent("2", transitions, TamaIVRMessage.START_SYMPTOM_FLOW);
    }

    private void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState){
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }

        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }

    private void assertAudioFilePresent(String keyPressed, Map<String, Transition> transitionsssed, String audioFilename){
        List<Prompt> prompts = transitions.get(keyPressed).getDestinationNode().getPrompts();
        List<String> promptNames = new ArrayList<String>();
        for (Prompt prompt : prompts) {
            if (prompt instanceof AudioPrompt && prompt.getName() != null){
                promptNames.add(prompt.getName());
            }
        }

        assertTrue(promptNames.contains(audioFilename));
    }
}
