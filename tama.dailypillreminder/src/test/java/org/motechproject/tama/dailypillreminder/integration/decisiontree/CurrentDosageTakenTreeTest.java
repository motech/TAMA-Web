package org.motechproject.tama.dailypillreminder.integration.decisiontree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.dailypillreminder.command.NextCallDetails;
import org.motechproject.tama.dailypillreminder.decisiontree.CurrentDosageTakenTree;
import org.motechproject.tama.ivr.command.IncomingWelcomeMessage;
import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class CurrentDosageTakenTreeTest {
    @Autowired
    private TestConfirmTree testConfirmTree;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void TearDown() {
        testConfirmTree.setTreeToNull();
    }

    @Test
    public void shouldGetPillTakenCommand() {
        Node nextNode = testConfirmTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(4, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof IncomingWelcomeMessage);
        assertTrue(prompts.get(1).getCommand() instanceof NextCallDetails);
        assertTrue(prompts.get(2).getCommand() instanceof SymptomAndOutboxMenuCommand);
    }

    @Test
    public void shouldTransitionToPullMessagesOnPressOf3() {
        HttpSession httpSession = mock(HttpSession.class);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        KooKooIVRContext kookooIVRContext = mock(KooKooIVRContext.class);

        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
        when(httpRequest.getSession()).thenReturn(httpSession);

        Node nextNode = testConfirmTree.getTree().nextNode("", "");

        new AssertUtil(kookooIVRContext, httpSession).assertCallStateTransitionForKeyPress("3", nextNode.getTransitions(), CallState.PULL_MESSAGES_TREE);
    }
}

@Component
class TestConfirmTree extends CurrentDosageTakenTree {
    @Autowired
    public TestConfirmTree(TAMATreeRegistry tamaTreeRegistry) {
        super(tamaTreeRegistry);
    }

    public void setTreeToNull() {
        tree = null;
    }
}