package org.motechproject.tama.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class FourDayRecallServiceTest {

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
    private PatientAlertService patientAlertService;

    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        fourDayRecallService = new FourDayRecallService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, patientAlertService);
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

        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId);

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

        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId);

        assertFalse(capturedForCurrentWeek);
    }

    @Test
    public void shouldGetAdherenceLogForPreviousWeek() {
        String treatmentAdviceID = "treatmentAdviceID";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);

        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);

        LocalDate logDate = DateUtil.newDate(2011, 10, 12);
        List<WeeklyAdherenceLog> logs = new ArrayList<WeeklyAdherenceLog>();
        logs.add(new WeeklyAdherenceLog(patientId, treatmentAdviceID, startDateOfTreatmentAdvice, logDate, 2));

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdviceID, new LocalDate(2011, 10, 5))).thenReturn(logs);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());
        when(treatmentAdvice.getId()).thenReturn(treatmentAdviceID);

        WeeklyAdherenceLog logForPreviousWeek = fourDayRecallService.getAdherenceLogForPreviousWeek(patientId);

        assertNotNull(logForPreviousWeek);
        assertEquals(logDate, logForPreviousWeek.getLogDate());
        assertEquals(patientId, logForPreviousWeek.getPatientId());
        assertEquals(treatmentAdviceID, logForPreviousWeek.getTreatmentAdviceId());
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

        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallService.isAdherenceBeingCapturedForFirstWeek(patientId);

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

        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallService.isAdherenceBeingCapturedForFirstWeek(patientId);

        assertFalse(adherenceBeingCapturedForFirstWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenPreferredDayIsSameAsTreatmentAdviceStartDay() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Sunday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 13);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenLessThanFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 11);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForAnyWeekSpecified() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 6).toDate();
        LocalDate today = new LocalDate(2011, 11, 25);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForAnyWeek(patientId, today);

        assertEquals(new LocalDate(2011, 11, 20), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheFourDayRecallDateForAnyWeekSpecified() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Friday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 11, 7).toDate();
        LocalDate today = new LocalDate(2011, 11, 29);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate fourDayRecallDateForCurrentWeek = fourDayRecallService.findFourDayRecallDateForAnyWeek(patientId, today);
        
        assertEquals(new LocalDate(2011, 11, 25), fourDayRecallDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeekWhenMoreThanFiveDaysIntoCurrentWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Thursday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 14);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnFirstRetryDay_AndFiveDaysIntoTheNextWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 13);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnSecondRetryDay_AndFiveDaysIntoTheNextWeek() {
        Patient patient = new Patient();
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Wednesday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2).toDate();
        LocalDate today = new LocalDate(2011, 10, 14);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        LocalDate startDateForCurrentWeek = fourDayRecallService.getStartDateForCurrentWeek(patientId);

        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldDetermineIfTheCurrentAdherenceIsFalling() {
        final int numberOfDaysMissed = 2;
        final String testPatientId = "testPatientId";
        FourDayRecallService fourDayRecallService = new FourDayRecallService(null, null, null, null) {
            @Override
            public int adherencePercentageFor(int daysMissed) {
                if (numberOfDaysMissed == daysMissed) return 23;
                return 0;
            }

            @Override
            public int adherencePercentageForPreviousWeek(String patientId) {
                if (patientId.equals(testPatientId)) return 34;
                return 0;
            }
        };
        assertTrue(fourDayRecallService.isAdherenceFalling(numberOfDaysMissed, testPatientId));
    }

    @Test
    public void shouldRaiseAnAlertIfAdherenceTrendIsFalling() {
        final String testPatientId = "testPatientId";
        FourDayRecallService fourDayRecallService = new FourDayRecallService(null, null, null, patientAlertService) {
            @Override
            public boolean isAdherenceFalling(int dosageMissedDays, String patientId) {
                if (patientId.equals(testPatientId)) return true;
                return false;
            }

            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
                weeklyAdherenceLog.setNumberOfDaysMissed(1);
                return weeklyAdherenceLog;
            }
        };
        fourDayRecallService.raiseAdherenceFallingAlert(testPatientId);
        verify(patientAlertService).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), any(PatientAlertType.class), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnTrue_WhenAdherenceAlertHasAlreadyBeenRaised() {
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
        assertEquals(true, fourDayRecallService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.getPatientPreferences().setDayOfWeeklyCall(DayOfWeek.Saturday);
        Date startDateOfTreatmentAdvice = new LocalDate(2011, 9, 27).toDate();
        LocalDate today = new LocalDate(2011, 10, 16);

        setupExpectations(patient, startDateOfTreatmentAdvice, today);

        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(false, fourDayRecallService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    private void setupExpectations(Patient patient, Date startDateOfTreatmentAdvice, LocalDate today) {
        when(DateUtil.today()).thenReturn(today);
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice);
        when(DateUtil.newDate(startDateOfTreatmentAdvice)).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
    }
}