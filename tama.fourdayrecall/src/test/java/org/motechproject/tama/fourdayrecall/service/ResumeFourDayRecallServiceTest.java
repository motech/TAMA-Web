package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Properties;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ResumeFourDayRecallServiceTest extends BaseUnitTest {
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private Properties properties;

    private ResumeFourDayRecallService resumeFourDayRecallService;
    private Patient patient;
    private LocalDate today;
    private TreatmentAdvice treatmentAdvice;

    @Before
    public void setUp() {
        initMocks(this);
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("2");
        today = new LocalDate(2011, 12, 27);

        String patientId = "patientId";
        patient = PatientBuilder.startRecording().withDefaults()
                .withCallPreference(CallPreference.FourDayRecall)
                .withId(patientId)
                .build();

        LocalDate treatmentStartDate = new LocalDate(2011, 11, 27);
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(treatmentStartDate).withDefaults().build();

        resumeFourDayRecallService = new ResumeFourDayRecallService(allWeeklyAdherenceLogs, properties, new FourDayRecallDateService());
    }

    private void timeToday(int hour, int minute) {
        DateTime now = DateUtil.newDateTime(today, new Time(hour, minute));
        mockCurrentDate(now);
    }

    private void adherenceNotCaptured(LocalDate weekStartDate) {
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDate)).thenReturn(null);
    }

    private void suspendedOn(int year, int month, int day, int hour, int minute) {
        patient.setLastSuspendedDate(DateUtil.newDateTime(new LocalDate(year, month, day), hour, minute, 0));
    }

    private void backFill(boolean doseTaken) {
        resumeFourDayRecallService.backFillAdherence(patient, treatmentAdvice, patient.getLastSuspendedDate(), DateUtil.now(), doseTaken);
    }

    private void logsRecorded(int count) {
        verify(allWeeklyAdherenceLogs, times(count)).add(Matchers.<WeeklyAdherenceLog>any());
    }

    private void callTime(int hour, int minute, TimeMeridiem timeMeridiem, DayOfWeek day) {
        PatientPreferences patientPreferences = new PatientPreferences();
        patientPreferences.setBestCallTime(new TimeOfDay(hour, minute, timeMeridiem));
        patientPreferences.setDayOfWeeklyCall(day);
        patient.setPatientPreferences(patientPreferences);
    }

    private void treatmentStartDate(LocalDate startDate) {
        treatmentAdvice.getDrugDosages().get(0).setStartDate(startDate);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateAndResumptionDateIsBeforeRecallDateTime() {
        callTime(10, 0, TimeMeridiem.PM, DayOfWeek.Tuesday);
        timeToday(16, 0);
        suspendedOn(2011, 12, 27, 8, 0);
        backFill(true);
        logsRecorded(0);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateIsAfterRecallDateTime_AndResumptionDateIsBeforeBestCallTimeOnFirstRetryDate_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(8, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 26, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateIsOnRecallDate_AfterBestCallTime_AndResumptionDateIsAfterBestCallTimeOnFirstRetryDate_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(12, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 26, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateIsOnRetryDate_BeforeBestCallTime_AndResumptionDateIsBeforeBestCallTimeOnLastRetryDate_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Sunday);
        timeToday(8, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 26, 8, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateIsOnSecondRetryDate_AfterBestCallTime_AndResumptionDateIsAfterBestCallTimeOnLastRetryDate_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Sunday);
        timeToday(16, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 26, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedResumedInTheSameWeek_AndSuspendedDateIsAfterSecondRetryDate_AfterBestCallTime_AndResumptionDateIsBeforeNextWeekRecallDateAndBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Tuesday);
        timeToday(8, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 23, 12, 0);
        backFill(true);
        logsRecorded(0);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsAfterSecondRetryDate_AfterBestCallTime_AndResumptionDateIsOnNextWeekRecallDateAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Tuesday);
        timeToday(12, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 23, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsAfterSecondRetryDate_AfterBestCallTime_AndIsResumedOnFirstRetryDateOfNextWeekAndBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(8, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 23, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsAfterSecondRetryDate_AfterBestCallTime_AndIsResumedOnFirstRetryDateOfNextWeekAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(12, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 23, 12, 0);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsOnRecallDate_AfterBestCallTime_AndIsResumedOnRecallDateOfNextWeekAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Tuesday);
        timeToday(12, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 18));
        suspendedOn(2011, 12, 20, 12, 0);
        backFill(true);
        logsRecorded(2);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsARecallDate_BeforeBestCallTime_AndIsResumedAfterLastRetryDateOfNextWeekAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.PM, DayOfWeek.Saturday);
        timeToday(23, 30);
        adherenceNotCaptured(new LocalDate(2011, 12, 11));
        suspendedOn(2011, 12, 17, 10, 0);
        backFill(true);
        logsRecorded(2);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsARetryDate_AndResumedAfterLastRetryDateOfNextWeekAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.PM, DayOfWeek.Saturday);
        timeToday(23, 30);
        adherenceNotCaptured(new LocalDate(2011, 12, 11));
        suspendedOn(2011, 12, 19, 15, 30);
        backFill(false);
        logsRecorded(2);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTwoWeeks_AndSuspendedDateIsAfterLastRetryDate_AndResumedAfterOnFirstRetryDateOfNextWeekAndBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 0, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(8, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 11));
        suspendedOn(2011, 12, 23, 15, 30);
        backFill(false);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverThreeWeeks_AndSuspendedDateIsAfterLastRetryDate_AndResumedBeforeRecallDateAndBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.PM, DayOfWeek.Tuesday);
        timeToday(8, 30);
        adherenceNotCaptured(new LocalDate(2011, 12, 4));
        suspendedOn(2011, 12, 16, 15, 30);
        backFill(true);
        logsRecorded(1);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverThreeWeeks_AndSuspendedDateIsAfterLastRetryDate_AndResumedBeforeRecallDateAndAfterBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Tuesday);
        timeToday(14, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 4));
        suspendedOn(2011, 12, 16, 15, 30);
        backFill(true);
        logsRecorded(2);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverThreeWeeks_AndSuspendedDateIsAfterLastRetryDate_AndResumptionDateIsOnFirstRetryDate_AndResumptionTimeIsBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(9, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 4));
        suspendedOn(2011, 12, 16, 15, 30);
        backFill(false);
        logsRecorded(2);

    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverThreeWeeks_AndSuspendedDateIsOnRecallDate_AndAfterBestCallTime_AndResumptionDateIsOnFirstRetryDate_AndResumptionTimeIsBeforeBestCallTime_AdherenceNotCaptured() {
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(9, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 4));
        suspendedOn(2011, 12, 12, 15, 30);
        backFill(false);
        logsRecorded(3);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTheFirstTreatmentAdviceWeek_AndRecallDateIsOnTheNextWeek_AdherenceNotCaptured() {
        treatmentStartDate(new LocalDate(2011, 12, 25));
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(11, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 25));
        suspendedOn(2011, 12, 26, 15, 30);
        backFill(false);
        logsRecorded(0);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTheFirstTreatmentAdviceWeek_AndSuspendedDateIsBeforeRecallDate_AndResumptionDateIsBeforeRecallDate_AdherenceNotCaptured() {
        treatmentStartDate(new LocalDate(2011, 12, 25));
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Friday);
        timeToday(11, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 25));
        suspendedOn(2011, 12, 26, 15, 30);
        backFill(false);
        logsRecorded(0);
    }

    @Test
    public void whenPatientIsSuspendedAndResumedOverTheFirstTreatmentAdviceWeek_AndSuspendedDateIsOnRecallDate_AfterBestCallTimeAndResumptionDateIsOnRetryDateAndAfterBestCallTime_AdherenceNotCaptured() {
        treatmentStartDate(new LocalDate(2011, 12, 19));
        callTime(10, 30, TimeMeridiem.AM, DayOfWeek.Monday);
        timeToday(11, 0);
        adherenceNotCaptured(new LocalDate(2011, 12, 19));
        suspendedOn(2011, 12, 26, 15, 30);
        backFill(false);
        logsRecorded(1);
    }
}