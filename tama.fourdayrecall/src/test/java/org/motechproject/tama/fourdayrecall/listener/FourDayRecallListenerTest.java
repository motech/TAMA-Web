package org.motechproject.tama.fourdayrecall.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.builder.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAlertService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.fourdayrecall.service.WeeklyAdherenceLogService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

/*TODO: verify behavior for no log exists*/
public class FourDayRecallListenerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String TREATMENT_ADVICE_ID = "TA_ID";

    @Mock
    FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    IVRCall ivrCall;
    @Mock
    FourDayRecallAlertService fourDayRecallAlertService;
    @Mock
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private TreatmentAdvice treatmentAdvice;
    @Mock
    private WeeklyAdherenceLogService weeklyAdherenceLogService;

    FourDayRecallListener fourDayRecallListener;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getId()).thenReturn(TREATMENT_ADVICE_ID);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.today().toDate());

        fourDayRecallListener = new FourDayRecallListener(ivrCall, fourDayRecallSchedulerService, fourDayRecallAlertService, new FourDayRecallDateService(), allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, weeklyAdherenceLogService);
    }

    @Test
    public void shouldMakeCallWhenNoLogExistsForCurrentWeek() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());

        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldMakeCallWhenLogForCurrentWeekHasNotRespondedStatus(){
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());

        WeeklyAdherenceLog log = new WeeklyAdherenceLog();
        log.setNotResponded(true);

        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(Matchers.<Patient>any(), Matchers.<TreatmentAdvice>any(), Matchers.<LocalDate>any())).thenReturn(log);
        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldScheduleRetryCallsOnFirstCall() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        setupLogsForWeek(patient, false);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());

        fourDayRecallListener.handle(motechEvent);

        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotScheduleCallsOnARetryCall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(true, true);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        setupLogsForWeek(patient, true);
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService, ivrCall);
    }

    @Test
    public void shouldNotMakeCallIfPatientIsSuspended() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, fourDayRecallSchedulerService, fourDayRecallAlertService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertWhenThereIsALogForThisWeek() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);
        setupLogsForWeek(patient, true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldRedAlertWhenThereIsALogForThisWeek() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);
        setupLogsForWeek(patient, true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAlertsWhenThereIsNoLogForTheWeek() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);
        setupLogsForWeek(patient, false);
        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verifyZeroInteractions(fourDayRecallAlertService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertOnLastRetryDay() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseRedAlertOnLastRetryDay() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertOnlyOnceForTheCurrentWeek() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);
        setupLogsForWeek(patient, true);
        when(fourDayRecallAlertService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService, never()).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseRedAlertOnlyOnceForTheCurrentWeek() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);
        setupLogsForWeek(patient, true);
        when(fourDayRecallAlertService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAlertService).hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID);
        verify(fourDayRecallAlertService, never()).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAlertsForASuspendedPatient() {
        boolean isLastRetry;
        setUpPatientWithDefaults();
        patient.setStatus(Status.Suspended);
        MotechEvent motechEvent = buildFourDayRecallEvent(false, isLastRetry = false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verifyNoMoreInteractions(fourDayRecallAlertService);
    }

    /*TODO: Verify the following tests*/
    @Test
    public void shouldCreateLogWithNotRespondedStatusOnFirstCall() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService).createNotRespondedLog(same(patient.getId()),anyInt());
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusOnRetryCalls() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(true, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()),anyInt());
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusIfPatientIsInActive() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Inactive).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()),anyInt());
    }

    @Test
    public void shouldNotCreateLogWithNotRespondedStatusIfAdherenceRecorded() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        MotechEvent motechEvent = buildFourDayRecallEvent(false, false);

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(Matchers.<Patient>any(), Matchers.<TreatmentAdvice>any(), Matchers.<LocalDate>any())).thenReturn(weeklyAdherenceLog);

        fourDayRecallListener.handle(motechEvent);
        verify(weeklyAdherenceLogService, never()).createNotRespondedLog(same(patient.getId()),anyInt());
    }

    private void setupLogsForWeek(Patient patient, boolean hasLogs) {
        WeeklyAdherenceLog log;
        if (hasLogs)
            log = new WeeklyAdherenceLog();
        else
            log = null;
        when(allWeeklyAdherenceLogs.findLogsByWeekStartDate(eq(patient), eq(treatmentAdvice), Matchers.<LocalDate>any())).thenReturn(log);
    }

    private void setUpPatientWithDefaults() {
        patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
    }

    private MotechEvent buildFourDayRecallEvent(boolean isRetry, boolean isLastRetry) {
        FourDayRecallEventPayloadBuilder dataBuilder = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID);
        if (isLastRetry || isRetry) dataBuilder.withRetryFlag(true);
        if (isLastRetry) dataBuilder.withLastRetryDayFlagSet();
        return new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, dataBuilder.payload());
    }
}