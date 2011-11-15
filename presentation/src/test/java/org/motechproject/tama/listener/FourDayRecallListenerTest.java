package org.motechproject.tama.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.call.IvrCall;
import org.motechproject.tama.platform.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallListenerTest {
    FourDayRecallListener fourDayRecallListener;

    @Mock
    TamaSchedulerService schedulerService;
    @Mock
    IvrCall ivrCall;
    @Mock
    private FourDayRecallService fourDayRecallService;
    @Mock
    private AllPatients allPatients;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener(ivrCall, schedulerService, fourDayRecallService, allPatients);
    }

    @Test
    public void shouldScheduleRetryCalls() {
        LocalDate startDate = DateUtil.today();
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Active).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withTreatmentAdviceId(TREATMENT_ADVICE_ID)
                .withTreatmentAdviceStartDate(startDate)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);

        fourDayRecallListener.handle(motechEvent);

        verify(schedulerService).scheduleRepeatingJobsForFourDayRecall(PATIENT_ID, TREATMENT_ADVICE_ID, startDate);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        LocalDate startDate = DateUtil.today();
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withTreatmentAdviceId(TREATMENT_ADVICE_ID)
                .withTreatmentAdviceStartDate(startDate)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(schedulerService, ivrCall);
    }

    @Test
    public void shouldNotCreateRetryJobsOnlyForSubsequentRetryCalls() {
        LocalDate startDate = DateUtil.today();
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Active).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withTreatmentAdviceId(TREATMENT_ADVICE_ID)
                .withTreatmentAdviceStartDate(startDate)
                .withRetryFlag(true)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(schedulerService);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldPostAlertIfAdherenceTrendIsFalling() {
        String PATIENT_ID = "patient_id";
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        fourDayRecallListener.handleWeeklyFallingAdherence(motechEvent);

        verify(fourDayRecallService).raiseAdherenceFallingAlert(PATIENT_ID);
    }

    @Test
    public void shouldNotCallIfPatientIsSuspended() {
        String PATIENT_ID = "patient_id";
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID).payload();
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, schedulerService, fourDayRecallService);
    }
}
