package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class IVRDayMessageBuilderTest {

    IVRDayMessageBuilder ivrDayMessageBuilder;

    @Before
    public void setup() {
        ivrDayMessageBuilder = new IVRDayMessageBuilder(new TamaIVRMessage(null));
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 0, 00, 00));
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
    }

    @Test
    public void shouldReturnWavFilesForTomorrowMorning() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("Num_010", messages.get(0));
        assertEquals("Num_020", messages.get(1));
        assertEquals("001_07_04_doseTimeAtMorning", messages.get(2));
        assertEquals("timeOfDayTomorrow", messages.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTomorrowEvening() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(19).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("Num_007", messages.get(0));
        assertEquals("Num_020", messages.get(1));
        assertEquals("001_07_04_doseTimeAtEvening", messages.get(2));
        assertEquals("timeOfDayTomorrow", messages.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTodayEvening() {
        DateTime time = DateUtil.now().withHourOfDay(19).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("Num_007", messages.get(0));
        assertEquals("Num_020", messages.get(1));
        assertEquals("001_07_04_doseTimeAtEvening", messages.get(2));
        assertEquals("timeOfDayToday", messages.get(3));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForTodayMorning() {
        DateTime time = DateUtil.now().withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(1, messages.size());
        assertEquals("001_07_02_doseTimeOfMorning", messages.get(0));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysMorning() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("001_07_02_doseTimeOfYesterdays", messages.get(0));
        assertEquals("001_07_02_doseTimeOfMorning", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdayInTheMorning() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("001_07_04_doseTimeAtYesterday", messages.get(0));
        assertEquals("001_07_04_doseTimeAtMorning", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysAfternoon() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(12).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("001_07_02_doseTimeOfYesterdays", messages.get(0));
        assertEquals("001_07_02_doseTimeOfAfternoon", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysEvening() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(16).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("001_07_02_doseTimeOfYesterdays", messages.get(0));
        assertEquals("001_07_02_doseTimeOfEvening", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForLastNight() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(20).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(1, messages.size());
        assertEquals("001_07_02_doseTimeOfLastnight", messages.get(0));
    }
}
