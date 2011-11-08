package org.motechproject.tama.platform.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;
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

    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        fourDayRecallService = new FourDayRecallService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs);
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
       FourDayRecallService fourDayRecallService = new FourDayRecallService(null, null, null) {
           @Override
           public int adherencePercentageFor(int daysMissed) {
              if(numberOfDaysMissed == daysMissed) return 23;
               return 0;
           }

           @Override
           public int adherencePercentageForPreviousWeek(String patientId) {
               if(patientId.equals(testPatientId)) return 34;
               return 0;
           }
       };
       assertTrue(fourDayRecallService.isAdherenceFalling(numberOfDaysMissed, testPatientId));
    }

    private void setupExpectations(Patient patient, Date startDateOfTreatmentAdvice, LocalDate today) {
        when(DateUtil.today()).thenReturn(today);
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice);
        when(DateUtil.newDate(startDateOfTreatmentAdvice)).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
    }
}