package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallAlertServiceTest extends BaseUnitTest {

    private String patientId = "patientId";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private TreatmentAdvice treatmentAdvice;
    @Mock
    private Properties properties;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private AdherenceService adherenceService;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private WeeklyAdherenceLogService weeklyAdherenceLogService;

    private FourDayRecallDateService fourDayRecallDateService;
    private FourDayRecallAlertService fourDayRecallAlertService;


    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallDateService = new FourDayRecallDateService();
        fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService, weeklyAdherenceLogService);
    }

    @Test
    public void shouldRaiseAlertIfAdherenceTrendIsFalling() throws NoAdherenceRecordedException {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

        when(fourDayRecallAdherenceService.getAdherencePercentageForCurrentWeek(patientId)).thenReturn(0);
        when(fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patientId)).thenReturn(10);

        fourDayRecallAlertService.raiseAdherenceFallingAlert(patientId);

        verify(patientAlertService).createAlert(eq(patientId), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.FALLING_ADHERENCE), eq("Adherence fell by 100.00%, from 10.00% to 0.00%"),
                eq(PatientAlertType.FallingAdherence), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldNotRaiseAlertForFirstWeek() throws NoAdherenceRecordedException {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

        when(fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patientId)).thenThrow(new NoAdherenceRecordedException("exception"));

        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 10, 5), 0, 0, 0));
        fourDayRecallAlertService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnTrueWhenAdherenceFallingAlertHasAlreadyBeenRaised() {
        PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(true, fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalseWhenAdherenceFallingAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(false, fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldRaiseRedAlertIfAdherenceIsLessThanThreshold() throws NoAdherenceRecordedException {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        String testPatientId = "testPatientId";

        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setNumberOfDaysMissed(2);

        when(weeklyAdherenceLogService.get(anyString(), anyInt())).thenReturn(weeklyAdherenceLog);
        when(fourDayRecallAdherenceService.adherencePercentageFor(weeklyAdherenceLog)).thenReturn(50);

        fourDayRecallAlertService.raiseAdherenceInRedAlert(testPatientId);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(50));

        verify(patientAlertService).createAlert(testPatientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                "Adherence percentage is 50.00%", PatientAlertType.AdherenceInRed, data);
    }

    @Test
    public void shouldRaiseNoResponseRedAlertIfNoResponseCaptured() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");

        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setNotResponded(true);

        when(weeklyAdherenceLogService.get(patientId, 0)).thenReturn(weeklyAdherenceLog);

        fourDayRecallAlertService.raiseAdherenceInRedAlert(patientId);

        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(0));
        verify(patientAlertService).createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE, PatientAlertType.AdherenceInRed, data);
    }

    @Test
    public void shouldNotRaiseRedAlertIfNoLogExists() throws NoAdherenceRecordedException {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");

        when(weeklyAdherenceLogService.get(patientId, 0)).thenReturn(null);
        when(fourDayRecallAdherenceService.adherencePercentageFor(null)).thenThrow(new NoAdherenceRecordedException("exception"));

        fourDayRecallAlertService.raiseAdherenceInRedAlert(patientId);

        verifyZeroInteractions(patientAlertService);
    }

    @Test
    public void shouldNotRaiseRedAlertIfAdherenceIsNotLessThanThreshold() throws NoAdherenceRecordedException {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";

        final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setNumberOfDaysMissed(1);

        when(weeklyAdherenceLogService.get(anyString(), anyInt())).thenReturn(weeklyAdherenceLog);
        when(fourDayRecallAdherenceService.adherencePercentageFor(weeklyAdherenceLog)).thenReturn(75);

        fourDayRecallAlertService.raiseAdherenceInRedAlert(testPatientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnTrueWhenAdherenceInRedAlertHasAlreadyBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};

        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(true, fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalseWhenAdherenceInRedAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        assertEquals(false, fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void hasAdherenceFallingAlertBeenRaisedForCurrentWeekShouldUsePreviousBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        ArgumentCaptor<DateTime> startDateCaptor = ArgumentCaptor.forClass(DateTime.class);

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), startDateCaptor.capture(), Matchers.<DateTime>any())).thenReturn(new PatientAlerts());

        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 24), 0, 0, 0));
        fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId);
        assertEquals(DateUtil.newDate(2011, 11, 18), startDateCaptor.getValue().toLocalDate());
    }
}
