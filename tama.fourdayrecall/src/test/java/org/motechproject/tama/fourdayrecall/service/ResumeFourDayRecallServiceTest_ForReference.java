package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
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
@Ignore
public class ResumeFourDayRecallServiceTest_ForReference {
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private Properties properties;

    private ResumeFourDayRecallService resumeFourDayRecallService;
    private static String DAYS_TO_RETRY = "2";

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
//        resumeFourDayRecallService = new ResumeFourDayRecallService(allWeeklyAdherenceLogs, allPatients, allTreatmentAdvices, properties, fourDayRecallAdherenceService);
    }

    @Test
    public void shouldRecordAdherenceForPastThreeWeeks() {
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        final DateTime firstRecallDateTime = new DateTime(2011, 11, 5, 22, 30, 0);

        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.setTimeZone(toDate.minusDays(16))).thenReturn(toDate.minusDays(16));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);
        DateTime suspendedDate = DateUtil.now().minusDays(16);
        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);

        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withWeeklyCallPreference(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(suspendedDate).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(allPatients.get(patientId)).thenReturn(testPatient);
        // method removed
//        when(fourDayRecallAdherenceService.isAdherenceCapturedForWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
//        when(fourDayRecallAdherenceService.fourDayRecallDateTime(any(String.class), any(LocalDate.class))).thenReturn(firstRecallDateTime);

        resumeFourDayRecallService.backFillAdherenceForPeriodOfSuspension(patientId, false);
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForLastWeekFallsOutsideSuspensionPeriod() {
        DateTime toDate = new DateTime(2011, 11, 26, 21, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);
        DateTime firstRecallDateTime = new DateTime(2011, 11, 5, 22, 30, 0);

        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.setTimeZone(toDate.minusDays(16))).thenReturn(toDate.minusDays(16));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withWeeklyCallPreference(DayOfWeek.Saturday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(DateUtil.now().minusDays(16)).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(allPatients.get(patientId)).thenReturn(testPatient);
        // method removed
//        when(fourDayRecallAdherenceService.isAdherenceCapturedForWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
//        when(fourDayRecallAdherenceService.fourDayRecallDateTime(any(String.class), any(LocalDate.class))).thenReturn(firstRecallDateTime);

        resumeFourDayRecallService.backFillAdherenceForPeriodOfSuspension(patientId, false);
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForOnlyTwoWeeksIfPatientsBestCallTimeForFirstWeekFallsOutsideSuspensionPeriod() {
        DateTime toDate = new DateTime(2011, 11, 26, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        DateTime firstRecallDateTime = new DateTime(2011, 11, 5, 22, 30, 0);
        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);

        when(DateUtil.now()).thenReturn(toDate);
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 11, 26));
        when(DateUtil.setTimeZone(toDate.minusDays(16))).thenReturn(toDate.minusDays(16));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withWeeklyCallPreference(DayOfWeek.Thursday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(DateUtil.now().minusDays(16)).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(allPatients.get(patientId)).thenReturn(testPatient);
        // method removed
//        when(fourDayRecallAdherenceService.isAdherenceCapturedForWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(true);
//        when(fourDayRecallAdherenceService.fourDayRecallDateTime(any(String.class), any(LocalDate.class))).thenReturn(firstRecallDateTime);

        resumeFourDayRecallService.backFillAdherenceForPeriodOfSuspension(patientId, false);
        verify(allWeeklyAdherenceLogs, times(2)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldRecordAdherenceForThreeWeeksIfPatientsAdherenceHasNotBeenCapturedForPreviousWeekAndTheSuspensionStartDateFallsInTheRetryInterval() {
        DateTime now = new DateTime(2011, 11, 27, 23, 0, 0);
        DateTime suspendedDate = new DateTime(2011, 11, 11, 23, 0, 0);
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        DateTime firstRecallDateTime = new DateTime(2011, 11, 5, 22, 30, 0);
        TimeOfDay patientBestCallTime = new TimeOfDay(10, 30, TimeMeridiem.PM);

        when(DateUtil.now()).thenReturn(now);
        when(DateUtil.today()).thenReturn(now.toLocalDate());
        when(DateUtil.setTimeZone(now.minusDays(16))).thenReturn(now.minusDays(16));
        when(DateUtil.newDate(any(Date.class))).thenReturn(treatmentAdviceStartDate);

        String patientId = "patientId";
        Patient testPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId)
                .withWeeklyCallPreference(DayOfWeek.Tuesday, patientBestCallTime).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(suspendedDate).build();
        TreatmentAdvice testTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentAdviceStartDate).build();

        setupExpectations(patientId, testPatient, testTreatmentAdvice);
        when(allPatients.get(patientId)).thenReturn(testPatient);
        // method removed
//        when(fourDayRecallAdherenceService.isAdherenceCapturedForWeek(any(String.class), any(String.class), any(LocalDate.class))).thenReturn(false);
//        when(fourDayRecallAdherenceService.fourDayRecallDateTime(any(String.class), any(LocalDate.class))).thenReturn(firstRecallDateTime);

        resumeFourDayRecallService.backFillAdherenceForPeriodOfSuspension(patientId, false);
        verify(allWeeklyAdherenceLogs, times(3)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    private void setupExpectations(String patientId, Patient testPatient, TreatmentAdvice testTreatmentAdvice) {
        when(allPatients.get(patientId)).thenReturn(testPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(testTreatmentAdvice);
        // method removed
//        when(fourDayRecallAdherenceService.getStartDateForWeek(any(String.class), any(LocalDate.class)))
//                .thenReturn(new LocalDate(2011, 11, 6))
//                .thenReturn(new LocalDate(2011, 11, 6))
//                .thenReturn(new LocalDate(2011, 11, 13))
//                .thenReturn(new LocalDate(2011, 11, 13))
//                .thenReturn(new LocalDate(2011, 11, 20))
//                .thenReturn(new LocalDate(2011, 11, 20));
        when(properties.getProperty(any(String.class))).thenReturn(DAYS_TO_RETRY);
    }
}