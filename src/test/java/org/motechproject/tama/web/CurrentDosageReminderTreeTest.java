package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.motechproject.tama.web.command.*;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationTestContext.xml"})
public class CurrentDosageReminderTreeTest {
    @Autowired
    private TestTree currentDosageReminderTree;
    @Autowired
    private ThreadLocalTargetSource threadLocalTargetSource;

    @After
    public void TearDown() {
        currentDosageReminderTree.setTreeToNull();
    }

    @Test
    public void shouldGetCurrentDosageMessagePrompt() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(IVRMessage.PILL_REMINDER_RESPONSE_MENU, prompts.get(1).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(1).getClass());
        assertTrue(nextNode.getTreeCommands().isEmpty());
    }

    @Test
    public void shouldGetPillTakenCommand() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof MessageOnPillTaken);
        assertTrue(prompts.get(1).getCommand() instanceof MessageFromPreviousDosage);
        assertTrue(prompts.get(2).getCommand() instanceof MessageForAdherenceWhenPreviousDosageCapturedCommand);
        assertEquals(StopTodaysRemindersCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPillGettingLateCommandAndPrompt() {
        setUpDataForPreviousDosage(true);

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
        assertEquals(2, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof MessageForMissedPillFeedbackCommand);
        assertEquals(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU, prompts.get(1).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(1).getClass());
        assertEquals(StopTodaysRemindersCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertEquals(UpdateAdherenceCommand.class, nextNode.getTreeCommands().get(1).getClass());
    }

    @Test
    public void shouldGetPromptForCarryingExtraPills() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(IVRMessage.PLEASE_CARRY_SMALL_BOX, prompts.get(0).getName());
        assertEquals(RecordDeclinedDosageReasonCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertTrue(prompts.get(1).getCommand() instanceof MessageForAdherenceWhenPreviousDosageCapturedCommand);
        assertTrue(prompts.get(2).getCommand() instanceof MessageFromPreviousDosage);
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandIfPatientHasNotTakenThePillForUnknownReason() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(RecordDeclinedDosageReasonCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertTrue(prompts.get(0).getCommand() instanceof MessageForAdherenceWhenPreviousDosageCapturedCommand);
        assertTrue(prompts.get(1).getCommand() instanceof MessageFromPreviousDosage);
    }

    @Test
    public void shouldNotJumpToPreviousDosageTreeIfPreviousDosageCaptured() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        assertFalse(nextNode.hasTransitions());
    }

    @Test
    public void shouldJumpToPreviousDosageTreeIfPreviousDosageNotCaptured() {
        setUpDataForPreviousDosage(false);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        assertTrue(nextNode.hasTransitions());
    }

    private void setUpDataForPreviousDosage(boolean isCaptured) {

        IVRSession ivrSession = mock(IVRSession.class);

        LocalDate previousDosageLastTakenDate = isCaptured ? DateUtil.today().minusDays(1) : DateUtil.today().minusDays(2);

        DosageResponse currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), null);
        DosageResponse previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), previousDosageLastTakenDate, null);

        List<DosageResponse> dosageResponses = Arrays.asList(currentDosage, previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);

        IVRRequest ivrRequest = new IVRRequest();

        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\"}", PillReminderCall.DOSAGE_ID, "currentDosageId"));
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(null);
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
    }
}

@Component
class TestTree extends CurrentDosageReminderTree {
    public void setTreeToNull() {
        tree = null;
    }
}
