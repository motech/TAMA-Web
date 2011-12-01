package org.motechproject.tamacallflow.integration.decisiontree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tamacallflow.ivr.command.*;
import org.motechproject.tamacallflow.ivr.decisiontree.PreviousDosageReminderTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationTAMACallFlowContext.xml")
public class PreviousDosageReminderTreeTest {
    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;

    @Test
    public void shouldGetCommandsToExecuteOnTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "1");
        assertEquals(2, nextNode.getTreeCommands().size());
        assertEquals(StopPreviousPillReminderCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(UpdatePreviousPillAdherenceCommand.class, nextNode.getTreeCommands().get(1).getClass());
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(MessageForAdherenceWhenPreviousDosageNotCapturedCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }

    @Test
    public void shouldGetCommandsToExecuteOnNotTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "3");
        assertEquals(2, nextNode.getTreeCommands().size());
        assertEquals(StopPreviousPillReminderCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(UpdatePreviousPillAdherenceCommand.class, nextNode.getTreeCommands().get(1).getClass());
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillNotTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(MessageForAdherenceWhenPreviousDosageNotCapturedCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }
}
