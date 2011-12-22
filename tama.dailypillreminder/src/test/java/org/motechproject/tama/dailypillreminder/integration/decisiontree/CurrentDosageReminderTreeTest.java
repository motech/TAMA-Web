package org.motechproject.tama.dailypillreminder.integration.decisiontree;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.command.*;
import org.motechproject.tama.dailypillreminder.decisiontree.CurrentDosageReminderTree;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class CurrentDosageReminderTreeTest {
    @Autowired
    private TestTree currentDosageReminderTree;
    private DailyPillReminderContextForTest context;

    @Before
    public void setUp() {
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        context.dosageId("currentDosageId");
    }

    @After
    public void tearDown() {
        currentDosageReminderTree.setTreeToNull();
    }

    @Test
    public void shouldGetCurrentDosageMessagePrompt() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(TamaIVRMessage.PILL_REMINDER_RESPONSE_MENU, prompts.get(1).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(1).getClass());
        assertTrue(nextNode.getTreeCommands().isEmpty());
    }

    @Test
    public void shouldGetPillTakenCommand() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "1");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof MessageOnPillTaken);
        assertTrue(prompts.get(1).getCommand() instanceof AdherenceMessageWhenPreviousDosageCapturedCommand);
        assertEquals(UpdateAdherenceAsCapturedForCurrentDosageCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPillDelayWarningCommand() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof PillsDelayWarning);
        assertEquals(UpdateAdherenceAsNotCapturedForCurrentDosageCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPillGettingLateCommandAndPrompt() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(PillsDelayWarning.class, prompts.get(0).getCommand().getClass());
    }

    @Test
    public void shouldGetPromptForRecordingReasonForNotTakingPill() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof MissedPillFeedbackCommand);
        assertEquals(TamaIVRMessage.DOSE_CANNOT_BE_TAKEN_MENU, prompts.get(1).getName());
        assertEquals(MenuAudioPrompt.class, prompts.get(1).getClass());
        assertEquals(UpdateAdherenceAsCapturedForCurrentDosageCommand.class, nextNode.getTreeCommands().get(0).getClass());
    }

    @Test
    public void shouldGetPromptForCarryingExtraPills() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(TamaIVRMessage.PLEASE_CARRY_SMALL_BOX, prompts.get(0).getName());
        assertEquals(RecordDeclinedDosageReasonCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertTrue(prompts.get(1).getCommand() instanceof AdherenceMessageWhenPreviousDosageCapturedCommand);
    }

    @Test
    public void shouldGetRecordResponseInTamaCommandIfPatientHasNotTakenThePillForUnknownReason() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "3");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(1, prompts.size());
        assertEquals(RecordDeclinedDosageReasonCommand.class, nextNode.getTreeCommands().get(0).getClass());
        assertTrue(prompts.get(0).getCommand() instanceof AdherenceMessageWhenPreviousDosageCapturedCommand);
    }

    @Test
    public void shouldNotJumpToPreviousDosageTreeIfPreviousDosageCaptured() {
        setUpDataForPreviousDosage(true);

        Node nextNode = currentDosageReminderTree.getTree().nextNode("/3", "2");
        assertFalse(nextNode.hasTransitions());
    }

    private void setUpDataForPreviousDosage(boolean isCaptured) {
        LocalDate previousDosageLastTakenDate = isCaptured ? DateUtil.today().minusDays(1) : DateUtil.today().minusDays(2);

        DosageResponse currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), null);
        DosageResponse previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), previousDosageLastTakenDate, null);

        List<DosageResponse> dosageResponses = Arrays.asList(currentDosage, previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);

        context.pillRegimen(pillRegimenResponse).callStartTime(DateUtil.newDateTime(DateUtil.today(), 14, 0, 0));
    }
}

@Component
class TestTree extends CurrentDosageReminderTree {
    @Autowired
    public TestTree(TAMATreeRegistry tamaTreeRegistry) {
        super(tamaTreeRegistry);
    }

    public void setTreeToNull() {
        tree = null;
    }
}