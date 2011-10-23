package org.motechproject.tama.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.platform.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.ivr.call.IvrCall;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallListenerTest {
    FourDayRecallListener fourDayRecallListener;

    @Mock
    TAMASchedulerService schedulerService;
    @Mock
    IvrCall ivrCall;
    @Mock
    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener(ivrCall, schedulerService, fourDayRecallService);
    }

    @Test
    public void shouldScheduleRetryCalls() {
        LocalDate startDate = DateUtil.today();
        String PATIENT_ID = "patient_id";
        String TREATMENT_ADVICE_ID = "TA_ID";

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withTreatmentAdviceId(TREATMENT_ADVICE_ID)
                .withTreatmentAdviceStartDate(startDate)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);
        when(fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, TREATMENT_ADVICE_ID)).thenReturn(false);

        verify(schedulerService).scheduleRepeatingJobsForFourDayRecall(PATIENT_ID, TREATMENT_ADVICE_ID, startDate);
        verify(ivrCall).makeCall(PATIENT_ID);
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

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientDocId(PATIENT_ID)
                .withTreatmentAdviceId(TREATMENT_ADVICE_ID)
                .withTreatmentAdviceStartDate(startDate)
                .withRetryFlag(true)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);

        verifyZeroInteractions(schedulerService);
        verify(ivrCall).makeCall(PATIENT_ID);
    }
}
