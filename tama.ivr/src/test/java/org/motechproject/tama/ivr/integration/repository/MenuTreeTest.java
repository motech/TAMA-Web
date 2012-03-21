package org.motechproject.tama.ivr.integration.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.IncomingWelcomeMessage;
import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.decisiontree.MenuTree;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationIVRContext.xml", inheritLocations = false)
public class MenuTreeTest {
    @Autowired
    private MenuTestTree menuTree;
    private TAMAIVRContextForTest context;

    @Before
    public void setUp() {
        context = new TAMAIVRContextForTest();
    }

    @After
    public void tearDown() {
        menuTree.setTreeToNull();
    }

    @Test
    public void shouldGetMenuTreePrompts() {
        Node nextNode = menuTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof IncomingWelcomeMessage);
        assertTrue(prompts.get(1).getCommand() instanceof SymptomAndOutboxMenuCommand);
        assertThat(prompts.get(2).getName(), is(TamaIVRMessage.HEALTH_TIPS_MENU_OPTION));
    }
}

@Component
class MenuTestTree extends MenuTree {
    @Autowired
    public MenuTestTree(TAMATreeRegistry tamaTreeRegistry) {
        super(tamaTreeRegistry);
    }

    public void setTreeToNull() {
        tree = null;
    }
}