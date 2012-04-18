package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PillsDelayWarningTest {
    private PillsDelayWarning pillsDelayWarning;
    private int retryInterval;
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        retryInterval = 15;
        pillsDelayWarning = new PillsDelayWarning(null, 5);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        LocalDate today = DateUtil.today();
        context.pillRegimen(pillRegimenResponse).callStartTime(DateUtil.newDateTime(today, 16, 0, 0)).retryInterval(retryInterval).preferredLanguage("en");
    }

    @Test
    public void shouldReturnPleaseTakeDoseMessageForNonLastReminder() {
        context.numberOfTimesReminderSent(0).totalNumberOfTimesToSendReminder(2);
        assertArrayEquals(new String[]{
                TamaIVRMessage.PLEASE_TAKE_DOSE,
                String.format("Num_%03d", retryInterval),
                TamaIVRMessage.CALL_AFTER_SOME_TIME},
                pillsDelayWarning.executeCommand(context));
    }

    @Test
    public void shouldReturnLastReminderWarningMessage_WithReminderLag_WhenLastReminder() {
        context.numberOfTimesReminderSent(1).totalNumberOfTimesToSendReminder(1).callDirection(CallDirection.Outbound);

        assertEquals("22:05", context.nextDose().getDoseTime().toLocalTime().toString("HH:mm"));
        String[] messages = pillsDelayWarning.executeCommand(context);
        assertEquals(7, messages.length);
        assertEquals(TamaIVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("timeOfDayToday", messages[1]);
        assertEquals("timeOfDayAt", messages[2]);
        assertEquals("Num_010", messages[3]);
        assertEquals("Num_010", messages[4]);
        assertEquals("timeofDayPM", messages[5]);
        assertEquals("005_04_03_WillCallAgain", messages[6]);
    }
}

