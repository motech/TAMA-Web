package org.motechproject.tama.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.service.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.service.SchedulerService;

import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallListenerTest  {

    FourDayRecallListener fourDayRecallListener;

    @Mock
    SchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallListener = new FourDayRecallListener();
    }

    @Test
    public void shouldScheduleRetryCalls() {
        Map<String, Object> data = new FourDayRecallEventPayloadBuilder().withJobId("job_id").withPatientId("patient_id").payload();
        MotechEvent event = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, data);
        fourDayRecallListener.handle(event);
    }
}