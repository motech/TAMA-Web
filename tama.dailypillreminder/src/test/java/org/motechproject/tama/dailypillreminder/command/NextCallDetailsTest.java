package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.common.util.FixedDateTimeSource;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;
import org.motechproject.util.datetime.DefaultDateTimeSource;

import static org.junit.Assert.assertEquals;

public class NextCallDetailsTest {
    private NextCallDetails nextCallDetails;
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        nextCallDetails = new NextCallDetails(null);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2010, 10, 10, 16, 0, 0));
        context.preferredLanguage("en");
        DateTimeSourceUtil.SourceInstance = new FixedDateTimeSource(DateUtil.newDateTime(new LocalDate(2010, 10, 10), 0, 0, 0));
    }

    @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {
        context.callDirection(CallDirection.Inbound);

        String[] messages = nextCallDetails.executeCommand(context);
        assertEquals(7, messages.length);
        assertEquals("010_04_01_nextDoseIs1", messages[0]);
        assertEquals("timeOfDayToday", messages[1]);
        assertEquals("timeOfDayAt", messages[2]);
        assertEquals("Num_010", messages[3]);
        assertEquals("Num_005", messages[4]);
        assertEquals("timeofDayPM", messages[5]);
        assertEquals("010_04_06_nextDoseIs2", messages[6]);
    }

    @Test
    public void shouldNotReturnNextCallDetailMessagesForRepeatMenu() {
        context.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);
        String[] messages = nextCallDetails.executeCommand(context);
        assertEquals(0, messages.length);
    }

    @After
    public void tearDown() {
        DateTimeSourceUtil.SourceInstance = new DefaultDateTimeSource();
    }
}
