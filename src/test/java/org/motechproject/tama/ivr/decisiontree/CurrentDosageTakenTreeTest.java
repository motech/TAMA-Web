package org.motechproject.tama.ivr.decisiontree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.web.command.NextCallDetails;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationTestContext.xml"})
public class CurrentDosageTakenTreeTest {


    @Autowired
    private TestConfirmTree testConfirmTree;

    @Autowired
    private ThreadLocalTargetSource threadLocalTargetSource;

    private IVRRequest ivrRequest;

    @Mock
    private IVRSession ivrSession;

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
        assertEquals(1, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof NextCallDetails);
    }

}

@Component
class TestConfirmTree extends CurrentDosageTakenTree {
    public void setTreeToNull() {
        tree = null;
    }
}
