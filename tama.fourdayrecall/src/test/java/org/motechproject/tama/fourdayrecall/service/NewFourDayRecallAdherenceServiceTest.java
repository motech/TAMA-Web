package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class NewFourDayRecallAdherenceServiceTest extends BaseUnitTest {

    private String patientId = "patientId";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private Properties properties;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    private AdherenceService adherenceService;

    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, patientAlertService, properties, adherenceService);
    }

    @Test
    public void shouldReturnTrueIfLogsAreBeingCapturedForFirstWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 10, 0));
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 10, 5)).build();
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Monday, null).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertTrue(fourDayRecallAdherenceService.isAdherenceBeingCapturedForFirstWeek(patientId));
    }

    @Test
    public void shouldReturnFalseIfLogsAreNotBeingCapturedForFirstWeek() {
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 10, 5)).build();
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Sunday, null).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertFalse(fourDayRecallAdherenceService.isAdherenceBeingCapturedForFirstWeek(patientId));
    }

    @Test
    public void shouldGetTheFourDayRecallDateForAnyWeekSpecified_WhenStartDayIsBeforeDayOfBestWeeklyCall() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 25), fourDayRecallAdherenceService.fourDayRecallDate(patientId, new LocalDate(2011, 11, 29)));
    }

    @Test
    public void shouldGetTheFourDayRecallDateForAnyWeekSpecified_WhenStartDayIsAfterDayOfBestWeeklyCall() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Tuesday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 9)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 8), fourDayRecallAdherenceService.fourDayRecallDate(patientId, new LocalDate(2011, 11, 9)));
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForTreatmentAdviceStarting2DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 9)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 18), fourDayRecallAdherenceService.firstFourDayRecallDate(patientId, new LocalDate(2011, 11, 9)));
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForAnyTreatmentAdviceStarting5DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 6)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 11), fourDayRecallAdherenceService.firstFourDayRecallDate(patientId, new LocalDate(2011, 11, 6)));
    }

    @Test
    public void hasAdherenceFallingAlertBeenRaisedForCurrentWeek_shouldUsePreviousBestCallDay() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 24), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        DateTime expectedStartDateTime = new DateTime(2011, 11, 18, 0, 0, 0);
        DateTime expectedEndDateTime = new DateTime(2011, 11, 24, 0, 0, 0);
        when(patientAlertService.getFallingAdherenceAlerts(patientId, expectedStartDateTime, expectedEndDateTime)).thenReturn(new PatientAlerts());

        fourDayRecallAdherenceService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId);
        verify(patientAlertService).getFallingAdherenceAlerts(patientId, expectedStartDateTime, expectedEndDateTime);
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAdvice_WhenTodayIsBeforeBestCallDay_AndBestCallDayIsWithin4DaysOfStartDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 8), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Wednesday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertTrue(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAdvice_WhenTodayIsAfterBestCallDay_AndBestCallIsAfter4DaysOfStartDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 12), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Wednesday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertTrue(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }

    @Test
    public void shouldReturnFalseIfCurrentWeekIsNotFirstWeekOfTreatmentAdvice() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 18), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, null).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        assertFalse(fourDayRecallAdherenceService.isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId));
    }
}