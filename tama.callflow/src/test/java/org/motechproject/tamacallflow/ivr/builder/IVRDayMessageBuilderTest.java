package org.motechproject.tamacallflow.ivr.builder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class IVRDayMessageBuilderTest {

    IVRDayMessageBuilder ivrDayMessageBuilder;

    @Before
    public void setup() {
        ivrDayMessageBuilder = new IVRDayMessageBuilder();
        PowerMockito.mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 0, 00, 00));
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
    }

    @Test
    public void shouldReturnWavFilesForTomorrowMorning() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time, "en");
        assertEquals("timeOfDayTomorrow", messages.get(0));
        assertEquals("timeOfDayAt", messages.get(1));
        assertEquals("Num_010", messages.get(2));
        assertEquals("Num_020", messages.get(3));
        assertEquals("timeofDayAM", messages.get(4));
    }

    @Test
    public void shouldReturnWavFilesForTodayEvening() {
        DateTime time = DateUtil.now().withHourOfDay(19).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time, "en");
        assertEquals("timeOfDayToday", messages.get(0));
        assertEquals("timeOfDayAt", messages.get(1));
        assertEquals("Num_007", messages.get(2));
        assertEquals("Num_020", messages.get(3));
        assertEquals("timeofDayPM", messages.get(4));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterday() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        String message = ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAY_IN_THE_MORNING(time);
        assertEquals("001_07_04_doseTimeAtYesterday", message);
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForMorning() {
        DateTime time = DateUtil.now().withHourOfDay(10).withMinuteOfHour(20);

        String message = ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(time);
        assertEquals("001_07_02_doseTimeOfMorning", message);
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForAfternoon() {
        DateTime time = DateUtil.now().withHourOfDay(12).withMinuteOfHour(00);

        String message = ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(time);
        assertEquals("001_07_02_doseTimeOfAfternoon", message);
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForEvening_Case1() {
        DateTime time = DateUtil.now().withHourOfDay(16).withMinuteOfHour(00);

        String message = ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(time);
        assertEquals("001_07_02_doseTimeOfEvening", message);
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForEvening_Case2() {
        DateTime time = DateUtil.now().withHourOfDay(20).withMinuteOfHour(00);

        String message = ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(time);
        assertEquals("001_07_02_doseTimeOfEvening", message);
    }
}
