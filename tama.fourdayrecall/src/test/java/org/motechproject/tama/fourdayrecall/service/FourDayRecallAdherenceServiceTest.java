package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.domain.AdherenceSummaryForAWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallAdherenceServiceTest extends BaseUnitTest {

    @Mock
    protected AdherenceService adherenceService;
    @Mock
    protected WeeklyAdherenceLogService weeklyAdherenceLogService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    protected FourDayRecallDateService fourDayRecallDateService;
    protected FourDayRecallAdherenceService fourDayRecallAdherenceService;
    protected final DateTime now;
    private final LocalDate today;

    public FourDayRecallAdherenceServiceTest() {
        fourDayRecallDateService = new FourDayRecallDateService();
        now = new DateTime(2011, 1, 1, 10, 0, 0, 0);
        today = now.toLocalDate();
        mockCurrentDate(now);
    }

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allTreatmentAdvices, allWeeklyAdherenceLogs, weeklyAdherenceLogService, fourDayRecallDateService, adherenceService);
    }

    @Test
    public void shouldReturnAdherencePercentage() {
        int numberOfDosesMissed = 2;
        int numberOfDosesSupposedToTake = 4;
        int adherence = (numberOfDosesMissed * 100) / numberOfDosesSupposedToTake;
        assertEquals(adherence, fourDayRecallAdherenceService.adherencePercentageFor(numberOfDosesMissed));
    }

    @Test
    public void shouldReturnMapOfWeekStartDateAndAdherencePercentageOverTime() {
        DateTime tuesday = DateUtil.newDateTime(new LocalDate(2012, 01, 03), 0, 0, 0);
        DateTime nextTuesday = tuesday.plusDays(7);
        WeeklyAdherenceLog threeDaysMissed = new WeeklyAdherenceLog("patientId", null, null, tuesday, 3);
        WeeklyAdherenceLog twoDaysMissed = new WeeklyAdherenceLog("patientId", null, null, nextTuesday, 2);
        when(allWeeklyAdherenceLogs.findAllByPatientId("patientId")).thenReturn(Arrays.asList(threeDaysMissed, twoDaysMissed));

        List<AdherenceSummaryForAWeek> adherenceForAWeek = fourDayRecallAdherenceService.getAdherenceOverTime("patientId");

        assertEquals(tuesday, adherenceForAWeek.get(0).getWeekStartDate());
        assertEquals(25.0, adherenceForAWeek.get(0).getPercentage());

        assertEquals(nextTuesday, adherenceForAWeek.get(1).getWeekStartDate());
        assertEquals(50.0, adherenceForAWeek.get(1).getPercentage());
    }

    @Test
    public void shouldReturnAdherencePercentageForPreviousWeek() throws NoAdherenceRecordedException {
        String patientId = "patientId";
        WeeklyAdherenceLog weeklyAdherenceLogForPreviousWeek = new WeeklyAdherenceLog();
        weeklyAdherenceLogForPreviousWeek.setNumberOfDaysMissed(0);
        weeklyAdherenceLogForPreviousWeek.setLogDate(DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0));
        weeklyAdherenceLogForPreviousWeek.setPatientId(patientId);

        when(weeklyAdherenceLogService.get(patientId, 1)).thenReturn(weeklyAdherenceLogForPreviousWeek);
        int adherence = fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patientId);
        assertEquals(100, adherence);
    }

    @Test
    public void shouldReturnFalseForAnyDoseMissedLastWeekInTheFirstWeek() {
        TreatmentAdvice adviceStartingOnSaturday = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today).build();
        Patient patient = PatientBuilder.startRecording().withId("patientDocumentId").withCallPreference(CallPreference.FourDayRecall)
                .withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(adviceStartingOnSaturday);
        assertFalse(fourDayRecallAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test(expected = NoAdherenceRecordedException.class)
    public void shouldRaiseExceptionWhenAdherenceLogDoesNotExist() throws NoAdherenceRecordedException {
        when(weeklyAdherenceLogService.get("patientId", 0)).thenReturn(null);
        fourDayRecallAdherenceService.getAdherencePercentageForCurrentWeek("patientId");
    }

    @Test
    public void shouldReturnZeroWhenAdherenceStatusIsNotAnswered() throws NoAdherenceRecordedException {
        String patientId = "patientId";
        WeeklyAdherenceLog notRespondedLog = new WeeklyAdherenceLog();
        notRespondedLog.setLogDate(DateUtil.newDateTime(today, 0, 0, 0));
        notRespondedLog.setNumberOfDaysMissed(1);
        notRespondedLog.setPatientId(patientId);
        notRespondedLog.setNotResponded(true);

        when(weeklyAdherenceLogService.get("patientId", 0)).thenReturn(notRespondedLog);
        assertEquals(0, fourDayRecallAdherenceService.getAdherencePercentageForCurrentWeek("patientId"));
    }

    @Test
    public void shouldReturnFalseForAnyDoseMissedLastWeekIfAdherenceStatusIsNotAnswered() {
        String patientId = "patientId";
        Patient patient = PatientBuilder.startRecording().withId(patientId).withCallPreference(CallPreference.FourDayRecall)
                .withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        WeeklyAdherenceLog notRespondedLog = new WeeklyAdherenceLog();
        notRespondedLog.setLogDate(DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0));
        notRespondedLog.setNumberOfDaysMissed(4);
        notRespondedLog.setPatientId(patientId);
        notRespondedLog.setNotResponded(true);

        TreatmentAdvice adviceStartingOnSaturday = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today.minusWeeks(2)).build();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(adviceStartingOnSaturday);
        when(weeklyAdherenceLogService.get(patient.getId(), 1)).thenReturn(notRespondedLog);

        assertFalse(fourDayRecallAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test
    public void shouldReturnTrueForAnyDoseMissedLastWeekIfPatientMissedADose() {
        TreatmentAdvice adviceStartingOnSaturday = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today.minusWeeks(2)).build();
        Patient patient = PatientBuilder.startRecording().withId("patientDocumentId").withCallPreference(CallPreference.FourDayRecall)
                .withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();

        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setLogDate(DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0));
        weeklyAdherenceLog.setNumberOfDaysMissed(1);
        weeklyAdherenceLog.setPatientId(patient.getId());


        when(weeklyAdherenceLogService.get(patient.getId(), 1)).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(adviceStartingOnSaturday);
        assertTrue(fourDayRecallAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test
    public void shouldReturnFalseForDoseTakeLate() {
        assertFalse(fourDayRecallAdherenceService.wasAnyDoseTakenLateLastWeek(null));
    }

    @Test
    public void shouldDetermineIfTheCurrentAdherenceIsFalling() {
        final int numberOfDaysMissed = 2;
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, null, adherenceService) {
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

            @Override
            public double getRunningAdherencePercentage(Patient patient) {
                return 0;
            }
        };
        assertTrue(fourDayRecallService.isAdherenceFalling(numberOfDaysMissed, testPatientId));
    }

    @Test
    public void shouldReturnTrue_WhenAdherenceIsCapturedForCurrentWeek() {
        final Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today.minusWeeks(2)).build();
        final LocalDate treatmentWeekStartDate = today.minusWeeks(1);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        final WeeklyAdherenceLog log = new WeeklyAdherenceLog() {{
            setNotResponded(false);
        }};
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patient, treatmentAdvice, treatmentWeekStartDate)).thenReturn(log);
        assertTrue(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceIsNotCapturedForCurrentWeek() {
        final Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today.minusWeeks(2)).build();
        final LocalDate treatmentWeekStartDate = today.minusWeeks(1);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patient, treatmentAdvice, treatmentWeekStartDate)).thenReturn(null);
        assertFalse(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient));
    }

    @Test
    public void shouldReturnFalse_WhenLogStatusIsSetAsNotResponded() {
        final Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today.minusWeeks(2)).build();
        final LocalDate treatmentWeekStartDate = today.minusWeeks(1);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        final WeeklyAdherenceLog log = new WeeklyAdherenceLog() {{
            setNotResponded(true);
        }};
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(patient, treatmentAdvice, treatmentWeekStartDate)).thenReturn(log);
        assertFalse(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient));
    }

}