package org.motechproject.tama.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationContext.xml"})
public class CurrentDosageReminderTreeTest {
    @Autowired
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Test
    public void shouldGetCurrentDosageMessagePrompt() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(IVRMessage.PILL_REMINDER_RESPONSE_MENU, prompts.get(2).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(2).getClass());
        assertTrue(nextNode.getTreeCommands().isEmpty());
    }

    @Test
    public void shouldGetFirstTimeReminderCommand() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(PillTakenCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPillGettingLateCommandAndPrompt() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(PillsDelayWarning.class, prompts.get(0).getCommand().getClass());
        assertEquals(MessageFromPreviousDosage.class, prompts.get(1).getCommand().getClass());
    }

    @Test
    public void shouldGetPromptForRecordingReasonForNotTakingPill() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU, prompts.get(0).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(0).getClass());
        assertEquals(UpdateAdherenceCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPromptForCarryingExtraPills() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(IVRMessage.PLEASE_CARRY_SMALL_BOX, prompts.get(0).getName());
        assertTrue(nextNode.getTreeCommands().isEmpty());
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandIfPatientHasNotTakenThePillForUnknownReason() {
        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(0, prompts.size());
        assertEquals(RecordResponseInTamaCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }
}
