package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

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
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        Properties stubProperties = new Properties();
        retryInterval = 15;
        pillsDelayWarning = new PillsDelayWarning(new IVRDayMessageBuilder(new TamaIVRMessage(null)), new TamaIVRMessage(null), null);
        context = new TAMAIVRContextForTest().pillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()).callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).retryInterval(retryInterval);
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
        assertEquals(6, messages.length);
        assertEquals(TamaIVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("Num_010", messages[1]);
        assertEquals("Num_005", messages[2]);
        assertEquals("001_07_04_doseTimeAtEvening", messages[3]);
        assertEquals("timeOfDayToday", messages[4]);
        assertEquals("005_04_03_WillCallAgain", messages[5]);
    }
}

