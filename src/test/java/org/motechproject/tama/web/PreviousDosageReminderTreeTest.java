package org.motechproject.tama.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.decisiontree.PreviousDosageReminderTree;
import org.motechproject.tama.web.command.MessageForAdherenceWhenPreviousDosageNotCapturedCommand;
import org.motechproject.tama.web.command.MessageOnPreviousPillNotTaken;
import org.motechproject.tama.web.command.MessageOnPreviousPillTaken;
import org.motechproject.tama.web.command.StopPreviousPillReminderCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationContext.xml"})
public class PreviousDosageReminderTreeTest {
    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;

    @Test
    public void shouldGetCommandsToExecuteOnTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "1");
        assertEquals(StopPreviousPillReminderCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(MessageForAdherenceWhenPreviousDosageNotCapturedCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }

    @Test
    public void shouldGetCommandsToExecuteOnNotTakingPreviousPill() {
        Node nextNode = previousDosageReminderTree.getTree().nextNode("/", "3");
        assertEquals(2, nextNode.getPrompts().size());
        assertEquals(MessageOnPreviousPillNotTaken.class, nextNode.getPrompts().get(0).getCommand().getClass());
        assertEquals(MessageForAdherenceWhenPreviousDosageNotCapturedCommand.class, nextNode.getPrompts().get(1).getCommand().getClass());
    }
}
