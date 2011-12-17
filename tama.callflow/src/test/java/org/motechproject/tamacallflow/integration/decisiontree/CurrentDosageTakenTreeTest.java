package org.motechproject.tamacallflow.integration.decisiontree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tamacallflow.ivr.command.NextCallDetails;
import org.motechproject.tamacallflow.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tamacallflow.ivr.decisiontree.CurrentDosageTakenTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-TAMACallFlow.xml", inheritLocations = false)
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
        assertEquals(3, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof NextCallDetails);
        assertTrue(prompts.get(1).getCommand() instanceof SymptomAndOutboxMenuCommand);
    }
}

@Component
class TestConfirmTree extends CurrentDosageTakenTree {
    public void setTreeToNull() {
        tree = null;
    }
}