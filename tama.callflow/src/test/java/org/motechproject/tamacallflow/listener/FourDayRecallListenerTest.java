package org.motechproject.tamacallflow.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.MotechEvent;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.Status;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
import org.motechproject.tamacallflow.ivr.call.IvrCall;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamacallflow.listener.FourDayRecallListener;
import org.motechproject.tamacallflow.platform.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallListenerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String TREATMENT_ADVICE_ID = "TA_ID";

    @Mock
    TamaSchedulerService schedulerService;
    @Mock
    IvrCall ivrCall;
    @Mock
    FourDayRecallService fourDayRecallService;
    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private TreatmentAdvice treatmentAdvice;

    FourDayRecallListener fourDayRecallListener;

    @Before
    public void setUp() {
        initMocks(this);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getId()).thenReturn(TREATMENT_ADVICE_ID);

        fourDayRecallListener = new FourDayRecallListener(ivrCall, schedulerService, fourDayRecallService, allPatients, allTreatmentAdvices);
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
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());

        fourDayRecallListener.handle(motechEvent);

        verify(schedulerService).scheduleRepeatingJobsForFourDayRecall(PATIENT_ID);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(schedulerService, ivrCall);
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

        verifyZeroInteractions(schedulerService);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotCallIfPatientIsSuspended() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, schedulerService, fourDayRecallService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlert_WhenAdherenceIsCaptured() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);

        verify(fourDayRecallService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAdherenceFallingAlert_WhenAdherenceIsNotCaptured() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);
        verify(fourDayRecallService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verifyNoMoreInteractions(fourDayRecallService);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlert_ForLastRetryDay_EvenWhenAdherenceIsNotCaptured() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withLastRetryDayFlagSet().payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);

        verify(fourDayRecallService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldRaiseAdherenceFallingAlert_ForLastRetryDay_WhenAdherenceIsCaptured() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withLastRetryDayFlagSet().payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);

        verify(fourDayRecallService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotRaiseAdherenceFallingAlert_WhenAlertHasAlreadyBeenCreated() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallService.hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID)).thenReturn(true);

        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);
        verify(fourDayRecallService).isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID);
        verify(fourDayRecallService).hasAdherenceFallingAlertBeenRaisedForCurrentWeek(PATIENT_ID);

        verifyNoMoreInteractions(fourDayRecallService);
    }
}
