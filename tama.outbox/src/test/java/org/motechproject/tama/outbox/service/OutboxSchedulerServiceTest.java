package org.motechproject.tama.outbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.*;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class OutboxSchedulerServiceTest {
    private static final String PATIENT_ID = "patient_id";

    private OutboxSchedulerService schedulerService;
    private Patient patient;

    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);
        final TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);
        patient = new Patient() {{
            setId(PATIENT_ID);
            getPatientPreferences().setBestCallTime(bestCallTime);
        }};
        schedulerService = new OutboxSchedulerService(motechSchedulerService, properties);
    }

    @Test
    public void shouldScheduleOutboxCall() {
        schedulerService.scheduleOutboxJobs(patient);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleJob(jobCaptor.capture());
        assertEquals("0 30 10 * * ?", jobCaptor.getValue().getCronExpression());
    }

    @Test
    public void shouldScheduleRepeatingJobsForOutboxCalls() {
        when(properties.getProperty(TAMAConstants.RETRIES_PER_DAY)).thenReturn("10");
        when(properties.getProperty(TAMAConstants.OUT_BOX_CALL_RETRY_INTERVAL)).thenReturn("15");

        schedulerService.scheduleRepeatingJobForOutBoxCall(patient);

        ArgumentCaptor<RepeatingSchedulableJob> jobCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRepeatingJob(jobCaptor.capture());
        assertEquals(9, jobCaptor.getValue().getRepeatCount().intValue());
        assertEquals(15 * 60 * 1000, jobCaptor.getValue().getRepeatInterval());
        final Map<String, Object> parameters = jobCaptor.getValue().getMotechEvent().getParameters();
        assertEquals(PATIENT_ID, parameters.get(EventKeys.SCHEDULE_JOB_ID_KEY));
        assertEquals(PATIENT_ID, parameters.get(OutboxSchedulerService.EXTERNAL_ID_KEY));
        assertEquals(Boolean.TRUE.toString(), parameters.get(OutboxSchedulerService.IS_RETRY));
    }
}
