package org.motechproject.tama.fourdayrecall.listener;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.fourdayrecall.builder.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAlertService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.fourdayrecall.service.WeeklyAdherenceLogService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallListenerTest extends BaseUnitTest {

    public static final String PATIENT_ID = "patientId";

    private DateTime NOW = DateUtil.now();
    @Mock
    IVRCall ivrCall;
    @Mock
    FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    FourDayRecallAlertService fourDayRecallAlertService;
    @Mock
    FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    AllPatients allPatients;
    @Mock
    private WeeklyAdherenceLogService weeklyAdherenceLogService;

    private FourDayRecallListener fourDayRecallListener;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener(ivrCall, fourDayRecallSchedulerService, fourDayRecallAlertService, fourDayRecallAdherenceService, allPatients, weeklyAdherenceLogService);
        mockCurrentDate(NOW);
    }

    @Test
    public void shouldMakeCallWhen_AdherenceHasNotBeenCapturedForCurrentWeek() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(false);
        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient, CallTypeConstants.FOUR_DAY_RECALL_CALL, new HashMap<String, String>());
    }

    @Test
    public void shouldNotMakeCallWhenScheduledTimeIsConfiguredMinutesGreaterThanCurrentTime() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        motechEvent.setScheduledTime(NOW.plusMinutes(16).toDate());

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(false);
        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall, never()).makeCall(patient, CallTypeConstants.FOUR_DAY_RECALL_CALL, new HashMap<String, String>());
    }

    @Test
    public void shouldNotMakeCallWhenScheduledTimeIsConfiguredMinutesLessThanCurrentTime() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        motechEvent.setScheduledTime(NOW.minusMinutes(16).toDate());

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(false);
        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall, never()).makeCall(patient, CallTypeConstants.FOUR_DAY_RECALL_CALL, new HashMap<String, String>());
    }

    @Test
    public void shouldScheduleRetryCallsOnFirstCall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, true);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient, CallTypeConstants.FOUR_DAY_RECALL_CALL, new HashMap<String, String>());
    }

    @Test
    public void shouldNotScheduleCallsOnARetryCall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(true, true, false);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService, ivrCall);
    }

    @Test
    public void shouldNotMakeCallIfPatientIsSuspended() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, fourDayRecallAlertService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertWhen_AdherenceCapturedForThisWeek() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(true);
        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAlertsWhenThereIsNoLogForTheWeek() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verifyZeroInteractions(fourDayRecallAlertService);
    }

    @Test
    public void shouldRaiseRedAlertWhen_AdherenceIsCapturedForTheCurrentWeek() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(true);
        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertOnLastRetryDay() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, true, false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceFallingAlert(PATIENT_ID);
    }


    @Test
    public void shouldRaiseRedAlertOnLastRetryDay() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, true, false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertOnlyOnceForTheCurrentWeek() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        when(fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService, never()).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseRedAlertOnlyOnceForTheCurrentWeek() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);
        when(fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(true);
        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID);
        verify(fourDayRecallAlertService, never()).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAlertsForASuspendedPatient() {
        setUpPatientWithDefaults();
        patient.setStatus(Status.Suspended);
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verifyNoMoreInteractions(fourDayRecallAlertService);
    }

    @Test
    public void shouldCreateLogWithNotRespondedStatusOnFirstCall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, true);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService).createNotRespondedLog(same(patient.getId()));
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusOnRepeatCalls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()));
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusOnRetryCalls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(true, false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()));
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusIfPatientIsInActive() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Inactive).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()));
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusIfAdherenceRecorded() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(patient)).thenReturn(true);
        fourDayRecallListener.handle(motechEvent);

        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()));
    }

    @Test
    public void shouldMakeCallWhenFirstCallFlagIsNotSet() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        FourDayRecallEventPayloadBuilder dataBuilder = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID);//.withFirstCall(false);
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, dataBuilder.payload());
        motechEvent.setScheduledTime(NOW.toDate());

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient, CallTypeConstants.FOUR_DAY_RECALL_CALL, new HashMap<String, String>());
    }

    private void setUpPatientWithDefaults() {
        patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
    }

    private MotechEvent buildFourDayRecallEvent(boolean isRetry, boolean isLastRetry, boolean isFirstCall) {
        FourDayRecallEventPayloadBuilder dataBuilder = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withFirstCall(isFirstCall);

        if (isLastRetry || isRetry) dataBuilder.withRetryFlag(true);
        if (isLastRetry) dataBuilder.withLastRetryDayFlagSet();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, dataBuilder.payload());
        motechEvent.setScheduledTime(NOW.toDate());
        return motechEvent;
    }
}