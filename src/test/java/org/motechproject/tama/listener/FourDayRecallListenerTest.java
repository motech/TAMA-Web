package org.motechproject.tama.listener;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.call.FourDayRecallCall;
import org.motechproject.tama.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.service.TamaSchedulerService;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallListenerTest {
    FourDayRecallListener fourDayRecallListener;

    @Mock
    TamaSchedulerService schedulerService;
    @Mock
    FourDayRecallCall fourDayRecallCall;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener(fourDayRecallCall, schedulerService);
    }

    @Test
    public void shouldScheduleRetryCalls() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusDays(10);
        String PATIENT_ID = "patient_id";

        Map<String, Object> data = new FourDayRecallEventPayloadBuilder()
                .withJobId("job_id")
                .withPatientId(PATIENT_ID)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .payload();
        MotechEvent motechEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(motechEvent);

        verify(schedulerService).scheduleRepeatingJobsForFourDayRecall(PATIENT_ID, startDate, endDate);
        verify(fourDayRecallCall).execute(PATIENT_ID);
    }
}
