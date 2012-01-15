package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

/*
 * TODO: Verify the new tests
 */
public class FourDayRecallAdherenceServiceTest extends BaseUnitTest {

    @Mock
    protected AdherenceService adherenceService;
    @Mock
    protected WeeklyAdherenceLogService weeklyAdherenceLogService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
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
        fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allTreatmentAdvices, weeklyAdherenceLogService, fourDayRecallDateService, adherenceService);
    }

    @Test
    public void shouldReturnAdherencePercentage() {
        int numberOfDosesMissed = 2;
        int numberOfDosesSupposedToTake = 4;
        int adherence = (numberOfDosesMissed * 100) / numberOfDosesSupposedToTake;
        assertEquals(adherence, fourDayRecallAdherenceService.adherencePercentageFor(numberOfDosesMissed));
    }

    @Test
    public void shouldReturnAdherencePercentageForPreviousWeek() {
        String patientId = "patientId";
        WeeklyAdherenceLog weeklyAdherenceLogForPreviousWeek = new WeeklyAdherenceLog();
        weeklyAdherenceLogForPreviousWeek.setNumberOfDaysMissed(0);
        weeklyAdherenceLogForPreviousWeek.setLogDate(today.minusWeeks(1));
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

    @Test
    public void shouldReturnZeroWhenAdherenceLogDoesNotExist() {
        when(weeklyAdherenceLogService.get("patientId", 0)).thenReturn(null);
        assertEquals(0, fourDayRecallAdherenceService.getAdherencePercentageForCurrentWeek("patientId"));
    }

    @Test
    public void shouldReturnZeroWhenAdherenceStatusIsNotAnswered() {
        String patientId = "patientId";
        WeeklyAdherenceLog notRespondedLog = new WeeklyAdherenceLog();
        notRespondedLog.setLogDate(today);
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
        notRespondedLog.setLogDate(today.minusWeeks(1));
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
        weeklyAdherenceLog.setLogDate(today.minusWeeks(1));
        weeklyAdherenceLog.setNumberOfDaysMissed(1);
        weeklyAdherenceLog.setPatientId(patient.getId());


        when(weeklyAdherenceLogService.get(patient.getId(), 1)).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(adviceStartingOnSaturday);
        assertTrue(fourDayRecallAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test
    public void shouldReturnFalseForDoseTakeLate() {
        assertFalse(fourDayRecallAdherenceService.wasAnyDoseTakenLateSince(null, null));
    }

    @Test
    public void shouldDetermineIfTheCurrentAdherenceIsFalling() {
        final int numberOfDaysMissed = 2;
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, adherenceService) {
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

}