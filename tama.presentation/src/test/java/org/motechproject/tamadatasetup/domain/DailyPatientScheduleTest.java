package org.motechproject.tamadatasetup.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DailyPatientScheduleTest {
    private PillReminderDataSetupConfiguration configuration;
    private DailyPatientSchedule dailyPatientSchedule;

    @Before
    public void setUp() {
        configuration = mock(PillReminderDataSetupConfiguration.class);
        when(configuration.morningDoseTime()).thenReturn(new Time(10, 10));
        dailyPatientSchedule = new DailyPatientSchedule(configuration, 10);
    }

    @Test
    public void nextDaysActivities() {
        when(configuration.treatmentAdviceGivenDate()).thenReturn(DateUtil.today());
        DailyPatientEvents dailyPatientEvents = dailyPatientSchedule.nextDaysActivities();
        assertEquals(DateUtil.today(), dailyPatientEvents.dateTime().toLocalDate());
    }

    @Test
    public void runAdherenceTrendJob() {
        when(configuration.treatmentAdviceGivenDate()).thenReturn(DateUtil.today());
        when(configuration.startFromDaysAfterTreatmentAdvice()).thenReturn(42);
        DailyPatientEvents dailyPatientEvents = dailyPatientSchedule.nextDaysActivities();
        assertEquals(true, dailyPatientEvents.runAdherenceTrendJob());
    }
}
