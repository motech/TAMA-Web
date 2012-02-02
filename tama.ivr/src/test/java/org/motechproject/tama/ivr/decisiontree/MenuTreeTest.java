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

    private MenuTree menuTree;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpSession httpSession;


    @Before
    public void setUp() {
        initMocks(this);
        setUpContext();
        menuTree = new MenuTree(new TAMATreeRegistry());
    }

    private void setUpContext() {
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    public void shouldTransitionToOutboxTreeWhenDTMFInputIs3() {
        assertTransitionToOutboxCommand(menuTree.createRootNode().getTransitions());
    }

    private void assertTransitionToOutboxCommand(Map<String, Transition> transitions) {
        List<ITreeCommand> treeCommands = transitions.get("3").getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }
        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(CallState.OUTBOX.toString()));
    }
}
