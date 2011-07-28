package org.motechproject.tama.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.decisiontree.PillReminderDecisionTree;
import org.motechproject.tama.web.command.PillTakenCommand;
import org.motechproject.tama.web.command.RecordResponseInTamaCommand;
import org.motechproject.tama.web.command.ScheduleCallCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationContext.xml"})
public class PillReminderDecisionTreeTest {
    @Autowired
    private PillReminderDecisionTree pillReminderTree;

    @Autowired
    private DecisionTreeService decisionTreeService;

    @Test
    public void shouldGetCurrentDosageMessagePrompt() {
        Node nextNode = pillReminderTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(IVRMessage.PILL_REMINDER_RESPONSE_MENU, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetFirstTimeReminderCommand() {
        Node nextNode = pillReminderTree.getTree().nextNode("/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(PillTakenCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetPillGettingLateCommandAndPrompt() {
        Node nextNode = pillReminderTree.getTree().nextNode("/", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(IVRMessage.PLEASE_TAKE_DOSE, prompts.get(0).getName());
        assertEquals(ScheduleCallCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetPromptForRecordingReasonForNotTakingPill() {
        Node nextNode = pillReminderTree.getTree().nextNode("/", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetPromptForCarryingExtraPills() {
        Node nextNode = pillReminderTree.getTree().nextNode("/3", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(IVRMessage.PLEASE_CARRY_SMALL_BOX, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandIfPatientHasNotTakenThePillForUnknownReason() {
        Node nextNode = pillReminderTree.getTree().nextNode("/3", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(0, prompts.size());
        assertEquals(RecordResponseInTamaCommand.class, nextNode.getTreeCommand().getClass());
    }
}
