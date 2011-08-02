package org.motechproject.tama.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.PreviousDosageReminderTree;
import org.motechproject.tama.web.command.PillTakenCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationContext.xml"})
public class PreviousDosageReminderTreeTest {
    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;

    @Test
    public void shouldGetOnPillTakenCommand() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(4, prompts.size());
        assertEquals(PillTakenCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPromptsOnNotTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(5, prompts.size());
        assertEquals(0, nextNode.getTreeCommands().size());
    }
}
