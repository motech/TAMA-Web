package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PillsDelayWarningTest {
    private PillsDelayWarning pillsDelayWarning;
    private int retryInterval;
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        retryInterval = 15;
        pillsDelayWarning = new PillsDelayWarning(new TamaIVRMessage(null), null);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        context.pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).retryInterval(retryInterval).preferredLanguage("en");
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
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
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {
        context.dosageId("currentDosageId").numberOfTimesReminderSent(1).totalNumberOfTimesToSendReminder(1).callDirection(CallDirection.Outbound);

        String[] messages = pillsDelayWarning.executeCommand(context);
        assertEquals(7, messages.length);
        assertEquals(TamaIVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("timeOfDayToday", messages[1]);
        assertEquals("timeOfDayAt", messages[2]);
        assertEquals("Num_010", messages[3]);
        assertEquals("Num_005", messages[4]);
        assertEquals("timeofDayPM", messages[5]);
        assertEquals("005_04_03_WillCallAgain", messages[6]);
    }
}
