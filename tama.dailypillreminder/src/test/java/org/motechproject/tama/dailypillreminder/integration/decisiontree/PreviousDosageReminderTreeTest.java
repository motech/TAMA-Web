package org.motechproject.tama.dailypillreminder.integration.decisiontree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageCommand;
import org.motechproject.tama.dailypillreminder.command.MessageOnPreviousPillNotTaken;
import org.motechproject.tama.dailypillreminder.command.MessageOnPreviousPillTaken;
import org.motechproject.tama.dailypillreminder.command.UpdateAdherenceAsCapturedForPreviousDosageCommand;
import org.motechproject.tama.dailypillreminder.decisiontree.PreviousDosageReminderTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class PreviousDosageReminderTreeTest {
    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;

    @Test
    public void shouldGetCommandsToExecuteOnTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "1");
        assertEquals(1, nextNode.getTreeCommands().size());
        assertEquals(UpdateAdherenceAsCapturedForPreviousDosageCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(AdherenceMessageCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }

    @Test
    public void shouldGetCommandsToExecuteOnNotTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "3");
        assertEquals(1, nextNode.getTreeCommands().size());
        assertEquals(UpdateAdherenceAsCapturedForPreviousDosageCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillNotTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(AdherenceMessageCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }
}
