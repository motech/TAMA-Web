package org.motechproject.tama.fourdayrecall.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.builder.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallListenerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String TREATMENT_ADVICE_ID = "TA_ID";

    @Mock
    FourDayRecallSchedulerService fourDayRecallSchedulerService;
    @Mock
    IVRCall ivrCall;
    @Mock
    FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private TreatmentAdvice treatmentAdvice;

    FourDayRecallListener fourDayRecallListener;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getId()).thenReturn(TREATMENT_ADVICE_ID);

        fourDayRecallListener = new FourDayRecallListener(ivrCall, fourDayRecallSchedulerService, fourDayRecallAdherenceService, allPatients, allTreatmentAdvices);
    }

    @Test
    public void shouldScheduleRetryCalls() {
        LocalDate startDate = DateUtil.today();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());

        fourDayRecallListener.handle(motechEvent);

        verify(fourDayRecallSchedulerService).scheduleRepeatingJobsForFourDayRecall(patient);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService, ivrCall);
    }

    @Test
    public void shouldNotCreateRetryJobsOnlyForSubsequentRetryCalls() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withRetryFlag(true)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(fourDayRecallSchedulerService);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotCallIfPatientIsSuspended() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, fourDayRecallSchedulerService, fourDayRecallAdherenceService);
    }

    private void setUpPatientWithDefaults() {
        patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
    }

    private MotechEvent getFourDayRecallEvent(boolean isLastRetryFlagSet) {
        FourDayRecallEventPayloadBuilder dataBuilder = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID);
        if (isLastRetryFlagSet) dataBuilder.withLastRetryDayFlagSet();
        return new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, dataBuilder.payload());
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertAndRedAlert_WhenAdherenceIsCaptured() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(false);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).raiseAdherenceFallingAlert(PATIENT_ID);
        verify(fourDayRecallAdherenceService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAdherenceFallingAlertOrRedAlert_WhenAdherenceIsNotCaptured() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(false);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verifyNoMoreInteractions(fourDayRecallAdherenceService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertAndRedAlert_ForLastRetryDay_EvenWhenAdherenceIsNotCaptured() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(true);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallAdherenceService).raiseAdherenceFallingAlert(PATIENT_ID);
        verify(fourDayRecallAdherenceService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlertAndRedAlert_ForLastRetryDay_WhenAdherenceIsCaptured() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(true);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallAdherenceService).raiseAdherenceFallingAlert(PATIENT_ID);
        verify(fourDayRecallAdherenceService).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAdherenceFallingAlert_WhenAlertHasAlreadyBeenCreated() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(false);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);
        when(fourDayRecallAdherenceService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallAdherenceService).hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID);
        verify(fourDayRecallAdherenceService, never()).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseRedAlert_WhenAlertHasAlreadyBeenCreated() {
        setUpPatientWithDefaults();
        MotechEvent motechEvent = getFourDayRecallEvent(false);
        when(fourDayRecallAdherenceService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);
        when(fourDayRecallAdherenceService.hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verify(fourDayRecallAdherenceService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallAdherenceService).hasAdherenceInRedAlertBeenRaisedForCurrentWeek(PATIENT_ID);
        verify(fourDayRecallAdherenceService, never()).raiseAdherenceInRedAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAdherenceFallingAlertOrRedAlert_WhenPatientIsSuspended() {
        setUpPatientWithDefaults();
        patient.setStatus(Status.Suspended);
        MotechEvent motechEvent = getFourDayRecallEvent(false);

        fourDayRecallListener.handleWeeklyFallingAdherenceAndRedAlert(motechEvent);

        verifyNoMoreInteractions(fourDayRecallAdherenceService);
    }

}
