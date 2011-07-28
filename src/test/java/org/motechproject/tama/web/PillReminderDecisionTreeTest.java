package org.motechproject.tama.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.tama.ivr.decisiontree.PillReminderTree;
import org.motechproject.tama.web.command.FirstTimeReminderCommand;
import org.motechproject.tama.web.command.RecordResponseInTamaCommand;
import org.motechproject.tama.web.command.ScheduleCallCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.motechproject.tama.ivr.decisiontree.PillReminderTree.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/applicationContext.xml"})
public class PillReminderDecisionTreeTest {
    @Autowired
    private PillReminderTree pillReminderTree;

    @Autowired
    private DecisionTreeService decisionTreeService;

    @Test
    public void shouldGetCurrentDosageMessagePrompt() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(AUDIO_RECORD_CURRENT_DOSAGE, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetFirstTimeReminderCommand() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(0, prompts.size());
        assertEquals(FirstTimeReminderCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandAndPromptIfCalledForTheFirstTime() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/1", "Yes");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(AUDIO_PILL_TAKEN_ON_TIME, prompts.get(0).getName());
        assertEquals(RecordResponseInTamaCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandForSubsequentCalls() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/1", "No");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(0, prompts.size());
        assertEquals(RecordResponseInTamaCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetPillGettingLateCommandAndPrompt() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(AUDIO_GETTING_LATE_FOR_PILL, prompts.get(0).getName());
        assertEquals(ScheduleCallCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldGetPromptForRecordingReasonForNotTakingPill() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(AUDIO_RECORD_REASON_FOR_NOT_TAKING_PILL, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetPromptForCarryingExtraPills() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/3", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(AUDIO_CARRY_EXTRA_PILLS, prompts.get(0).getName());
        assertNull(nextNode.getTreeCommand());
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandIfPatientHasNotTakenThePillForUnknownReason() {
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getTree(), "/3", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(0, prompts.size());
        assertEquals(RecordResponseInTamaCommand.class, nextNode.getTreeCommand().getClass());
    }
}
