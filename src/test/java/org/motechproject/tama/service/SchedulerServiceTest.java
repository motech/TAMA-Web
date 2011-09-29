package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerServiceTest {
    @Mock
    MotechSchedulerService motechSchedulerService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleJobsForFourDayRecallCalls() {
        SchedulerService schedulerService = new SchedulerService(motechSchedulerService);
        schedulerService.scheduleJobsForFourDayRecall("jobId", "externalId", DateUtil.newDate(2011, 9, 29), DayOfWeek.Friday, new Time(10, 30));

        ArgumentCaptor<CronSchedulableJob> argumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(argumentCaptor.capture());
        assertEquals("0 30 10 ? * 6", argumentCaptor.getValue().getCronExpression());
    }
}