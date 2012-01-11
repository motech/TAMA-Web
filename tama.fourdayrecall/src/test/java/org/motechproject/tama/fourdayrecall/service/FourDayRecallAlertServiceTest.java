package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallAlertServiceTest extends BaseUnitTest {

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
    @Mock
    private AdherenceService adherenceService;

    private FourDayRecallDateService fourDayRecallDateService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallDateService = new FourDayRecallDateService();
    }

    @Test
    public void shouldRaiseAnAlertIfAdherenceTrendIsFalling() {
        Patient patient = PatientBuilder.startRecording().withId(patientId).withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService) {

            @Override
            protected int getAdherencePercentageForCurrentWeek(String patientId) {
                return 0;
            }

            @Override
            public int getAdherencePercentageForPreviousWeek(String patientId) {
                return 10;
            }
        };
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        fourDayRecallAlertService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService).createAlert(eq(patientId), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.FALLING_ADHERENCE), eq("Adherence fell by 100.00%, from 10.00% to 0.00%"),
                eq(PatientAlertType.FallingAdherence), Matchers.<Map<String, String>>any());
    }


    @Test
    public void shouldNotRaiseAnAlertForFirstWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 10, 5), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        fourDayRecallAlertService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnTrue_WhenAdherenceFallingAlertHasAlreadyBeenRaised() {
        PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        assertEquals(true, fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceFallingAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        assertEquals(false, fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldRaiseARedAlertIfAdherenceIsLessThanThreshold() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(null, null, null, fourDayRecallDateService, adherenceService) {

            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
                weeklyAdherenceLog.setNumberOfDaysMissed(2);
                return weeklyAdherenceLog;
            }
        };
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        fourDayRecallAlertService.raiseAdherenceInRedAlert(testPatientId);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(50));
        verify(patientAlertService).createAlert(testPatientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                "Adherence percentage is 50.00%", PatientAlertType.AdherenceInRed, data);

    }

    @Test
    public void shouldRaiseANoResponseRedAlertIfNoResponseCaptured() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(null, null, null, fourDayRecallDateService, adherenceService) {
            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                return null;
            }
        };
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        fourDayRecallAlertService.raiseAdherenceInRedAlert(testPatientId);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(0));
        verify(patientAlertService).createAlert(testPatientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT,
                PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE, PatientAlertType.AdherenceInRed, data);

    }

    @Test
    public void shouldNotRaiseRedAlertIfAdherenceIsNotLessThanThreshold() {
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(null, null, null, fourDayRecallDateService, adherenceService) {

            @Override
            protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
                final WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
                weeklyAdherenceLog.setNumberOfDaysMissed(1);
                return weeklyAdherenceLog;
            }
        };
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        fourDayRecallAlertService.raiseAdherenceInRedAlert(testPatientId);
        verify(patientAlertService, never()).createAlert(Matchers.<String>any(), Matchers.<Integer>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<PatientAlertType>any(), Matchers.<Map<String, String>>any());
    }

    @Test
    public void shouldReturnTrue_WhenAdherenceInRedAlertHasAlreadyBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts() {{
            add(new PatientAlert());
        }};
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        assertEquals(true, fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void shouldReturnFalse_WhenAdherenceInRedAlertHasNotBeenRaised() {
        final PatientAlerts patientAlerts = new PatientAlerts();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 9, 27)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(patientAlertService.getAdherenceInRedAlerts(eq(patientId), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(patientAlerts);
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);
        assertEquals(false, fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(patientId));
    }

    @Test
    public void hasAdherenceFallingAlertBeenRaisedForCurrentWeek_shouldUsePreviousBestCallDay() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 24), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        ArgumentCaptor<DateTime> startDateCaptor = ArgumentCaptor.forClass(DateTime.class);
        when(patientAlertService.getFallingAdherenceAlerts(eq(patientId), startDateCaptor.capture(), Matchers.<DateTime>any())).thenReturn(new PatientAlerts());
        FourDayRecallAdherenceService fourDayRecallAdherenceService = new FourDayRecallAdherenceService(allPatients, allTreatmentAdvices, null, fourDayRecallDateService, adherenceService);
        FourDayRecallAlertService fourDayRecallAlertService = new FourDayRecallAlertService(allPatients, allTreatmentAdvices, properties, patientAlertService, fourDayRecallDateService, fourDayRecallAdherenceService);

        fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(patientId);
        assertEquals(DateUtil.newDate(2011, 11, 18), startDateCaptor.getValue().toLocalDate());
    }
}
