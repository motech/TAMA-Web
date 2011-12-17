package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tamacallflow.domain.WeeklyAdherenceLog;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamacallflow.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class FourDayRecallAdherenceServiceTest {
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private FourDayRecallService fourDayRecallService;
    @Mock
    private Properties properties;

    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private static String DAYS_TO_RETRY = "2";


    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allWeeklyAdherenceLogs, allPatients, allTreatmentAdvices, fourDayRecallService, properties);
    }

    @Test
    public void shouldRecordAdherenceForPastThreeWeeks() {
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 5));

        fourDayRecallAdherenceService.recordAdherence(SuspendedAdherenceDataPreset.fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForLastWeekFallsOutsideSuspensionPeriod() {
        DateTime toDate = new DateTime(2011, 11, 26, 21, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 5));

        fourDayRecallAdherenceService.recordAdherence(SuspendedAdherenceDataPreset.fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForFirstWeekFallsOutsideSuspensionPeriod() {
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Thursday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 10));

        fourDayRecallAdherenceService.recordAdherence(SuspendedAdherenceDataPreset.fromWeekBeforeLastWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForThreeWeeksIfPatientsAdherenceHasNotBeenCapturedForPreviousWeekAndTheSuspensionStartDateFallsInTheRetryInterval() {
        DAYS_TO_RETRY = "3";
        DateTime toDate = new DateTime(2011, 11, 27, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 27));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withPatientPreferences(DayOfWeek.Thursday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(fourDayRecallService.isAdherenceCapturedForAnyWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(false);
        when(fourDayRecallService.findFourDayRecallDateForAnyWeek(any(String.class), any(LocalDate.class))).thenReturn(new LocalDate(2011, 11, 10));

        fourDayRecallAdherenceService.recordAdherence(SuspendedAdherenceDataPreset.fromFourteenDaysBackWithAnyStatus(toDate));
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    private void setupExpectations(String patientId, Patient testPatient, TreatmentAdvice testTreatmentAdvice) {
        when(allPatients.get(patientId)).thenReturn(testPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(testTreatmentAdvice);
        when(fourDayRecallService.getStartDateForAnyWeek(any(String.class), any(LocalDate.class)))
                .thenReturn(new LocalDate(2011, 11, 6))
                .thenReturn(new LocalDate(2011, 11, 6))
                .thenReturn(new LocalDate(2011, 11, 13))
                .thenReturn(new LocalDate(2011, 11, 13))
                .thenReturn(new LocalDate(2011, 11, 20))
                .thenReturn(new LocalDate(2011, 11, 20));
        when(properties.getProperty(any(String.class))).thenReturn(DAYS_TO_RETRY);
    }

}