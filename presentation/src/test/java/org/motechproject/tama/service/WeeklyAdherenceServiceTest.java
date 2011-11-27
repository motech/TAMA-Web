package org.motechproject.tama.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tama.preset.SuspendedAdherenceDataPreset.fromWeekBeforeLastWithAnyStatus;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class WeeklyAdherenceServiceTest {
    @Mock
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    FourDayRecallService fourDayRecallService;

    WeeklyAdherenceService weeklyAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        weeklyAdherenceService = new WeeklyAdherenceService(allWeeklyAdherenceLogs, allPatients, allTreatmentAdvices, fourDayRecallService);
    }

    @Test
    public void shouldRecordAdherenceForPastThreeWeeks(){
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(new LocalDate(2011, 11, 6)).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 5));

        weeklyAdherenceService.recordAdherence(fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForLastWeekFallsOutsideSuspensionPeriod(){
        DateTime toDate = new DateTime(2011, 11, 26, 21, 0, 0);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(new LocalDate(2011, 11, 6)).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 5));

        weeklyAdherenceService.recordAdherence(fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForFirstWeekFallsOutsideSuspensionPeriod(){
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Thursday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(new LocalDate(2011, 11, 6)).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 10));

        weeklyAdherenceService.recordAdherence(fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForThreeWeeksIfPatientsAdherenceHasNotBeenCapturedForPreviousWeekAndTheSuspensionStartDateFallsInTheRetryInterval(){
        DateTime toDate = new DateTime(2011, 11, 27, 23, 0, 0);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 27));

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Thursday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(new LocalDate(2011, 11, 6)).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(false);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 10));

        weeklyAdherenceService.recordAdherence(fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }
    
    private void setupExpectations(String patientId, Patient testPatient, TreatmentAdvice testTreatmentAdvice) {
        when(allPatients.get(patientId)).thenReturn(testPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(testTreatmentAdvice);
        when(fourDayRecallService.getStartDateForAnyWeek(any(String.class), any(LocalDate.class)))
                                 .thenReturn(new LocalDate(2011, 11, 6))
                                 .thenReturn(new LocalDate(2011, 11, 13))
                                 .thenReturn(new LocalDate(2011, 11, 20));
    }

}