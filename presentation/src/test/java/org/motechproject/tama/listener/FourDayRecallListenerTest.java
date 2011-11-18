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
import org.motechproject.tama.domain.Status;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.ivr.call.IvrCall;
import org.motechproject.tama.platform.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.mock;
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
    FourDayRecallService fourDayRecallService;
    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatmentAdvices allTreatmentAdvices;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener(ivrCall, schedulerService, fourDayRecallService, allPatients, allTreatmentAdvices);
    }

    @Test
    public void shouldScheduleRetryCalls() {
        LocalDate startDate = DateUtil.today();
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);
        TreatmentAdvice treatmentAdvice = mock(TreatmentAdvice.class);
        when(treatmentAdvice.getStartDate()).thenReturn(startDate.toDate());
        when(treatmentAdvice.getId()).thenReturn(TREATMENT_ADVICE_ID);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);

        fourDayRecallListener.handle(motechEvent);

        verify(schedulerService).scheduleRepeatingJobsForFourDayRecall(PATIENT_ID);
        verify(ivrCall).makeCall(patient);
    }

    @Test
    public void shouldNotScheduleRetryCallsIfAdherenceIsAlreadyCaptured() {
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(true);

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        TreatmentAdvice treatmentAdvice = mock(TreatmentAdvice.class);
        when(treatmentAdvice.getId()).thenReturn(TREATMENT_ADVICE_ID);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(schedulerService, ivrCall);
    }

    @Test
    public void shouldNotCreateRetryJobsOnlyForSubsequentRetryCalls() {
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Active).withId(PATIENT_ID).build();

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withRetryFlag(true)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId(PATIENT_ID);
        treatmentAdvice.setId(TREATMENT_ADVICE_ID);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);

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
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);
        Mockito.verifyZeroInteractions(ivrCall, schedulerService, fourDayRecallService);
    }
}
