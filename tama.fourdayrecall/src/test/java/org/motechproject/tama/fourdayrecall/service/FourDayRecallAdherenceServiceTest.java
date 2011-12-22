package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class FourDayRecallAdherenceServiceTest {

    private String patientId = "patientId";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private TreatmentAdvice treatmentAdvice;
    @Mock
    private Properties properties;
    @Mock
    private TreatmentAdviceService treatmentAdviceService;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, patientAlertService, properties);
    }

    @Test
    public void shouldReturnTrueIfAdherenceIsCapturedForCurrentWeek() {
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 10, 5);
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.newDate(treatmentAdvice.getStartDate())).thenReturn(treatmentAdviceStartDate);
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        ArrayList<WeeklyAdherenceLog> adherenceLogs = new ArrayList<WeeklyAdherenceLog>() {{
            this.add(new WeeklyAdherenceLog());
        }};
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdviceId, startDateForWeek)).thenReturn(adherenceLogs);

        boolean capturedForCurrentWeek = fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId);

        assertTrue(capturedForCurrentWeek);
    }

    @Test
    public void shouldReturnFalseIfAdherenceIsNotCapturedForCurrentWeek() {
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 10, 5);
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.newDate(treatmentAdvice.getStartDate())).thenReturn(treatmentAdviceStartDate);
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        ArrayList<WeeklyAdherenceLog> adherenceLogs = new ArrayList<WeeklyAdherenceLog>();
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdviceId, startDateForWeek)).thenReturn(adherenceLogs);

        boolean capturedForCurrentWeek = fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId);

        assertFalse(capturedForCurrentWeek);
    }

    @Test
    public void shouldReturnTrueIfLogsAreBeingCapturedForFirstWeek() {
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate today = new LocalDate(2011, 10, 10);

        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Monday);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());

        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallAdherenceService.isAdherenceBeingCapturedForFirstWeek(patientId);

        assertTrue(adherenceBeingCapturedForFirstWeek);
    }

    @Test
    public void shouldReturnFalseIfLogsAreNotBeingCapturedForFirstWeek() {
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate today = new LocalDate(2011, 10, 16);

        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Sunday);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());

        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallAdherenceService.isAdherenceBeingCapturedForFirstWeek(patientId);

        assertFalse(adherenceBeingCapturedForFirstWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenPreferredDayIsSameAsTreatmentAdviceStartDay() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Sunday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 13);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenLessThanFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 11);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForAnyWeekSpecified() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 6).toDate();
        LocalDate today = new LocalDate(2011, 11, 25);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForAnyWeek(patientId, today);

        assertEquals(new LocalDate(2011, 11, 20), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheFourDayRecallDateForAnyWeekSpecified_WhenStartDayIsBeforeDayOfBestWeeklyCall() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 7).toDate();
        LocalDate today = new LocalDate(2011, 11, 29);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate fourDayRecallDateForCurrentWeek = fourDayRecallAdherenceService.findFourDayRecallDateForAnyWeek(patientId, today);

        assertEquals(new LocalDate(2011, 11, 25), fourDayRecallDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheFourDayRecallDateForAnyWeekSpecified_WhenStartDayIsAfterDayOfBestWeeklyCall() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Tuesday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 9).toDate();
        LocalDate today = new LocalDate(2011, 11, 9);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate fourDayRecallDateForCurrentWeek = fourDayRecallAdherenceService.findFourDayRecallDateForAnyWeek(patientId, today);

        assertEquals(new LocalDate(2011, 11, 8), fourDayRecallDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForTreatmentAdviceStarting2DaysBeforeBestCallDay() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 9).toDate();

        setupExpectations(patient, startDateOfTreatmentAdvice, DateUtil.newDate(startDateOfTreatmentAdvice));

        LocalDate firstFourDayRecallDateForTreatmentAdvice = fourDayRecallAdherenceService.findFirstFourDayRecallDateForTreatmentAdvice(patientId, DateUtil.newDate(treatmentAdvice.getStartDate()));

        assertEquals(new LocalDate(2011, 11, 18), firstFourDayRecallDateForTreatmentAdvice);
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForAnyTreatmentAdviceStarting5DaysBeforeBestCallDay() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 6).toDate();

        setupExpectations(patient, startDateOfTreatmentAdvice, DateUtil.newDate(startDateOfTreatmentAdvice));

        LocalDate firstFourDayRecallDateForTreatmentAdvice = fourDayRecallAdherenceService.findFirstFourDayRecallDateForTreatmentAdvice(patientId, DateUtil.newDate(treatmentAdvice.getStartDate()));

        assertEquals(new LocalDate(2011, 11, 11), firstFourDayRecallDateForTreatmentAdvice);
    }

    @Test
    public void shouldReturnTrueIfDateIsAtLeast4DaysAfterStartDate() {
        assertTrue(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 8), new LocalDate(2011, 11, 12)));
        assertTrue(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 8), new LocalDate(2011, 11, 13)));
        assertFalse(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 8), new LocalDate(2011, 11, 11)));
        assertFalse(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 8), new LocalDate(2011, 11, 8)));
        assertTrue(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 11), new LocalDate(2011, 11, 16)));
        assertFalse(fourDayRecallAdherenceService.isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(new LocalDate(2011, 11, 12), new LocalDate(2011, 11, 8)));
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenMoreThanFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 14);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnFirstRetryDay_AndFiveDaysIntoTheNextWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 13);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnSecondRetryDay_AndFiveDaysIntoTheNextWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 14);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallAdherenceService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldDetermineIfTheCurrentAdherenceIsFalling() {
        final int numberOfDaysMissed = 2;
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, null, properties) {
            @Override
            public int adherencePercentageFor(int daysMissed) {
                if (numberOfDaysMissed == daysMissed) return 23;
                return 0;
            }

            @Override
            public int getAdherencePercentageForPreviousWeek(String patientId) {
                if (patientId.equals(testPatientId)) return 34;
                return 0;
            }
        };
        assertTrue(fourDayRecallService.isAdherenceFalling(numberOfDaysMissed, testPatientId));
    }

    @Test
    public void shouldRaiseAnAlertIfAdherenceTrendIsFalling() {
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, patientAlertService, properties) {

            @Override
            protected int getAdherencePercentageForCurrentWeek(String patientId) {
                return 0;
            }

            @Override
            public int getAdherencePercentageForPreviousWeek(String patientId) {
                return 10;
            }

            @Override
            public boolean isCurrentWeekTheFirstWeekOfTreatmentAdvice(String patientId) {
                return false;
            }
        };
        fourDayRecallService.raiseAdherenceFallingAlert(testPatientId);
        verify(patientAlertService).createAlert(eq(testPatientId), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.FALLING_ADHERENCE), eq("Adherence fell by 100.00%, from 10.00% to 0.00%"),
                eq(PatientAlertType.FallingAdherence), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotRaiseAnAlertForFirstWeek() {
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, patientAlertService, properties) {

            @Override
            public boolean isCurrentWeekTheFirstWeekOfTreatmentAdvice(String patientId) {
                return true;
            }
        };
        fourDayRecallService.raiseAdherenceFallingAlert(testPatientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnToday_WhenTodaysDayIsBestCallDay() {
        Patient patient = new PatientBuilder().build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        LocalDate startDate = new LocalDate(2011, 11, 7);
        LocalDate today = new LocalDate(2011, 11, 25);
        setupExpectations(patient, startDate.toDate(), today);

        assertEquals(today, fourDayRecallAdherenceService.getMostRecentBestCallDay(patientId));
    }

    @Test
    public void shouldReturnLastBestCallDay_WhenTodayIsOneDayAfterBestCallDay() {
        Patient patient = new PatientBuilder().build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        LocalDate startDate = new LocalDate(2011, 11, 7);
        LocalDate expectedBestCallDay = new LocalDate(2011, 11, 25);
        LocalDate today = new LocalDate(2011, 11, 26);
        setupExpectations(patient, startDate.toDate(), today);

        assertEquals(expectedBestCallDay, fourDayRecallAdherenceService.getMostRecentBestCallDay(patientId));
    }

    @Test
    public void shouldReturnLastBestCallDay_WhenTodayIsOneDayBeforeBestCallDay() {
        Patient patient = new PatientBuilder().build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        LocalDate startDate = new LocalDate(2011, 11, 7);
        LocalDate expectedBestCallDay = new LocalDate(2011, 11, 18);
        LocalDate today = new LocalDate(2011, 11, 24);
        setupExpectations(patient, startDate.toDate(), today);

        assertEquals(expectedBestCallDay, fourDayRecallAdherenceService.getMostRecentBestCallDay(patientId));
    }

    @Test
    public void hasAdherenceFallingAlertBeenRaisedForCurrentWeek_shouldUsePreviousBestCallDay() {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 7).toDate();
        LocalDate today = new LocalDate(2011, 11, 24);
        LocalDate expectedStartDate = new LocalDate(2011, 11, 18);
        DateTime expectedStartDateTime = new DateTime(2011, 11, 18, 0, 0, 0);
        DateTime expectedEndDateTime = new DateTime(2011, 11, 24, 0, 0, 0);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);
        when(DateUtil.newDateTime((expectedStartDate), 0, 0, 0)).thenReturn(expectedStartDateTime);
        when(DateUtil.now()).thenReturn(expectedEndDateTime);
        when(patientAlertService.getFallingAdherenceAlerts(patientId, expectedStartDateTime, expectedEndDateTime)).thenReturn(new PatientAlerts());


        fourDayRecallAdherenceService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId);
        verify(patientAlertService).getFallingAdherenceAlerts(patientId, expectedStartDateTime, expectedEndDateTime);
    }

    @Test
    public void hasAdherenceInRedAlertBeenRaisedForCurrentWeek_shouldUsePreviousBestCallDay() {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 7).toDate();
        LocalDate today = new LocalDate(2011, 11, 24);
        LocalDate expectedStartDate = new LocalDate(2011, 11, 18);
        DateTime expectedStartDateTime = new DateTime(2011, 11, 18, 0, 0, 0);
        DateTime expectedEndDateTime = new DateTime(2011, 11, 24, 0, 0, 0);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);
        when(DateUtil.newDateTime((expectedStartDate), 0, 0, 0)).thenReturn(expectedStartDateTime);
        when(DateUtil.now()).thenReturn(expectedEndDateTime);
        when(patientAlertService.getAdherenceInRedAlerts(patientId, expectedStartDateTime, expectedEndDateTime)).thenReturn(new PatientAlerts());


        fourDayRecallAdherenceService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId);
        verify(patientAlertService).getAdherenceInRedAlerts(patientId, expectedStartDateTime, expectedEndDateTime);
    }

    @Test
    public void shouldReturnTrue_WhenAdherenceFallingAlertHasAlreadyBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Saturday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 9, 27).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(true, fourDayRecallAdherenceService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceFallingAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Saturday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 9, 27).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(false, fourDayRecallAdherenceService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldRaiseARedAlertIfAdherenceIsLessThanThreshold() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, patientAlertService, properties) {

            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
                weeklyAdherenceLog.setNumberOfDaysMissed(2);
                return weeklyAdherenceLog;
            }
        };
        fourDayRecallService.raiseAdherenceInRedAlert(testPatientId);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(50));
        verify(patientAlertService).createAlert(testPatientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                "Adherence percentage is 50.00%", PatientAlertType.AdherenceInRed, data);

    }

    @Test
    public void shouldRaiseANoResponseRedAlertIfNoResponseCaptured() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, patientAlertService, properties) {
            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                return null;
            }
        };
        fourDayRecallService.raiseAdherenceInRedAlert(testPatientId);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(0));
        verify(patientAlertService).createAlert(testPatientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE, PatientAlertType.AdherenceInRed, data);

    }

    @Test
    public void shouldNotRaiseRedAlertIfAdherenceIsNotLessThanThreshold() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, patientAlertService, properties) {

            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
                weeklyAdherenceLog.setNumberOfDaysMissed(1);
                return weeklyAdherenceLog;
            }
        };
        fourDayRecallService.raiseAdherenceInRedAlert(testPatientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());

    }

    @Test
    public void shouldReturnTrue_WhenAdherenceInRedAlertHasAlreadyBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Saturday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 9, 27).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(true, fourDayRecallAdherenceService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceInRedAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Saturday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 9, 27).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(false, fourDayRecallAdherenceService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAdvice_WhenTodayIsBeforeBestCallDay_AndBestCallDayIsWithin4DaysOfStartDate() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 7);
        LocalDate today = new LocalDate(2011, 11, 8);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), today);

        assertTrue(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAdvice_WhenTodayIsAfterBestCallDay_AndBestCallIsAfter4DaysOfStartDate() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 7);
        LocalDate today = new LocalDate(2011, 11, 12);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), today);

        assertTrue(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }

    @Test
    public void shouldReturnFalseIfCurrentWeekIsNotFirstWeekOfTreatmentAdvice() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 7);
        LocalDate today = new LocalDate(2011, 11, 18);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), today);

        assertFalse(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }

    @Test
    public void shouldReturnTransitionDateAsStartDate_WhenThereIsATransition() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 1, 1);
        LocalDate transitionDay = new LocalDate(2011, 11, 10);
        DateTime transitionDateTime = transitionDay.toDateTimeAtCurrentTime();

        Patient patient = new Patient();
        patient.getPatientPreferences().setCallPreferenceTransitionDate(transitionDateTime);
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), null);

        assertEquals(transitionDay, fourDayRecallAdherenceService.getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTreatmentAdviseStartDateAsStartDate_WhenThereIsNoTransition() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 12, 1);

        Patient patient = new Patient();
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), null);

        assertEquals(treatmentAdviceStartDate, fourDayRecallAdherenceService.getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldTestFourDayRecallRetryEndDateForFirstCall() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 12, 1);
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        patient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), treatmentAdviceStartDate.plusDays(3));
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("2");
        assertEquals(new DateTime(2011, 12, 11, 10, 10), fourDayRecallAdherenceService.getFirstWeeksFourDayRecallRetryEndDate(patient));
    }

    @Test
    public void shouldTestFourDayRecallRetryEndDateForFirstCallWhenPatientMovedFromDailyToFDR() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 1);
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        patient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
        patient.getPatientPreferences().setCallPreferenceTransitionDate(new LocalDate(2011, 12, 1).toDateTimeAtCurrentTime());
        setupExpectations(patient, treatmentAdviceStartDate.toDate(), treatmentAdviceStartDate.plusDays(3));

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("2");
        assertEquals(new DateTime(2011, 12, 11, 10, 10), fourDayRecallAdherenceService.getFirstWeeksFourDayRecallRetryEndDate(patient));
    }

    private void setupExpectations(Patient patient, Date startDateOfTreatmentAdvice, LocalDate today) {
        when(DateUtil.today()).thenReturn(today);
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice);
        when(DateUtil.newDate(startDateOfTreatmentAdvice)).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
    }

}