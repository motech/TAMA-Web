package org.motechproject.tama.outbox.service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.patient.domain.*;

import java.util.Properties;

import static org.mockito.Mockito.verify;
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
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        CallPreference callPreference = CallPreference.DailyPillReminder;
        patientPreferences.setCallPreference(callPreference);
        schedulerService.scheduleOutboxJobs(patient);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 30 10 * * ?", jobCaptor.getValue().getCronExpression());
    }
}
