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
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class SchedulerServiceTest {
    private SchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;
    private LocalDate treatmentAdviceStartDate = DateUtil.newDate(2012, 12, 12);
    private LocalDate treatmentAdviceEndDate = DateUtil.newDate(2012, 12, 24);

    private static final String PATIENT_ID = "patient_id";
    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private AllPatients allPatients;

    @Mock
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    @Mock
    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);

        treatmentAdvice = getTreatmentAdvice();

        Patient patient = new Patient();
        patient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        schedulerService = new SchedulerService(motechSchedulerService, pillReminderService, allPatients, pillRegimenRequestMapper, properties);
    }

    @Test
    public void shouldCreatePillRegimenRequestForPatientsOnDailyCalls() {
        schedulerService.scheduleJobsForTreatmentAdviceCalls(treatmentAdvice);
        verify(pillReminderService).createNew(any(DailyPillRegimenRequest.class));
    }

    @Test
    public void shouldScheduleFourDayRecallJobs() {
        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);
        patient.getPatientPreferences().setBestCallTime(bestCallTime);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(properties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("15");
        when(properties.getProperty(TAMAConstants.MAX_OUTBOUND_RETRIES)).thenReturn("3");

        schedulerService.scheduleJobsForTreatmentAdviceCalls(treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        assertEquals("0 30 10 ? * 6", cronSchedulableJobArgumentCaptor.getValue().getCronExpression());
        assertEquals(treatmentAdviceStartDate.plusDays(4).toDate(), cronSchedulableJobArgumentCaptor.getValue().getStartTime());
        assertEquals(treatmentAdviceEndDate.toDate(), cronSchedulableJobArgumentCaptor.getValue().getEndTime());

        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(motechSchedulerService).scheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        assertEquals(15, repeatingSchedulableJobArgumentCaptor.getValue().getRepeatInterval());
        assertEquals(new Integer(3), repeatingSchedulableJobArgumentCaptor.getValue().getRepeatCount());
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob() {
        schedulerService.scheduleJobsForTreatmentAdviceCalls(treatmentAdvice);

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