package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.*;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TamaSchedulerServiceTest {
    private TamaSchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;
    private LocalDate treatmentAdviceStartDate = DateUtil.newDate(2012, 12, 12);
    private LocalDate treatmentAdviceEndDate = DateUtil.newDate(2012, 12, 24);
    private Patient patient;

    private static final String PATIENT_ID = "patient_id";
    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);

        treatmentAdvice = getTreatmentAdvice();
        patient = new Patient();
        schedulerService = new TamaSchedulerService(motechSchedulerService, properties);
    }

    @Test
    public void shouldScheduleFourDayRecallJobs() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);
        int numDaysToRetry = 2;

        patient.getPatientPreferences().setBestCallTime(bestCallTime);
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        
        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(numDaysToRetry)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();
        CronSchedulableJob cronSchedulableJob1 = cronSchedulableJobList.get(0);
        assertEquals("0 30 10 ? * 6", cronSchedulableJob1.getCronExpression());
        assertEquals(treatmentAdviceStartDate.plusDays(4).toDate(), cronSchedulableJob1.getStartTime());
        assertEquals(treatmentAdviceEndDate.toDate(), cronSchedulableJob1.getEndTime());
        CronSchedulableJob cronSchedulableJob2 = cronSchedulableJobList.get(1);
        assertEquals("0 30 10 ? * 7", cronSchedulableJob2.getCronExpression());
        assertEquals(treatmentAdviceStartDate.plusDays(5).toDate(), cronSchedulableJob2.getStartTime());
        assertEquals(treatmentAdviceEndDate.toDate(), cronSchedulableJob2.getEndTime());
    }

    @Test
    public void shouldScheduleRepeatingJobsForFourDayRecall() {
        when(properties.getProperty(TAMAConstants.RETRIES_PER_DAY)).thenReturn("5");
        when(properties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("15");

        LocalDate today = DateUtil.today();
        LocalDate tenDaysLater = today.plusDays(10);
        schedulerService.scheduleRepeatingJobsForFourDayRecall(PATIENT_ID, today, tenDaysLater);

        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(motechSchedulerService).scheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        assertEquals(new Integer(5), repeatingSchedulableJobArgumentCaptor.getValue().getRepeatCount());
        assertEquals(15, repeatingSchedulableJobArgumentCaptor.getValue().getRepeatInterval());
        assertEquals(today.toDate(), repeatingSchedulableJobArgumentCaptor.getValue().getStartTime());
        assertEquals(tenDaysLater.toDate(), repeatingSchedulableJobArgumentCaptor.getValue().getEndTime());
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob() {
        schedulerService.scheduleJobForAdherenceTrendFeedback(treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }

    private TreatmentAdvice getTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setId("treatmentAdviceId");
        treatmentAdvice.setPatientId(PATIENT_ID);
        ArrayList<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        DrugDosage drugDosage = new DrugDosage();
        treatmentAdvice.setDrugCompositionGroupId("");
        drugDosage.setStartDate(treatmentAdviceStartDate);
        drugDosage.setEndDate(treatmentAdviceEndDate);
        drugDosages.add(drugDosage);
        treatmentAdvice.setDrugDosages(drugDosages);
        return treatmentAdvice;
    }

}