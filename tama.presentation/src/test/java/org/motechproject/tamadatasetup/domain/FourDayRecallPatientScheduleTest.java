package org.motechproject.tamadatasetup.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallPatientScheduleTest {
    @Test
    public void events() {
        FourDayRecallSetupConfiguration configuration = mock(FourDayRecallSetupConfiguration.class);
        when(configuration.adherenceResponse()).thenReturn("1,2");
        when(configuration.startFromWeeksAfterTreatmentAdvice()).thenReturn(2);
        when(configuration.treatmentAdviceGivenDate()).thenReturn(new LocalDate(2011, 11, 25));
        Time bestCallTime = new Time(10, 15);
        when(configuration.bestCallTime()).thenReturn(bestCallTime);

        FourDayRecallPatientSchedule schedule = new FourDayRecallPatientSchedule(configuration);
        assertEquals(2, schedule.numberOfWeeks());
        FourDayRecallPatientEvents events = schedule.events();
        assertEquals(true, events.runFallingTrendJob());
        assertEquals(bestCallTime.getHour().intValue(), events.callTime().getHourOfDay());
        assertEquals(bestCallTime.getMinute().intValue(), events.callTime().getMinuteOfHour());
    }
}
