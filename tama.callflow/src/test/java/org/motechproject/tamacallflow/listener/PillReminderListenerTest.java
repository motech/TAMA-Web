package org.motechproject.tamacallflow.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacallflow.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderListenerTest {
    @Mock
    private PillReminderCall pillReminderCall;

    private MotechEvent motechEvent;
    private PillReminderListener listener;

    @Before
    public void setUp() {
        initMocks(this);
        listener = new PillReminderListener(pillReminderCall);
    }

    @Test
    public void shouldExecutePillReminderCallWithPatientIdAndDosageId() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EventKeys.EXTERNAL_ID_KEY, "patientId");
        map.put(EventKeys.DOSAGE_ID_KEY, "dosageId");
        map.put(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND, 4);
        map.put(EventKeys.PILLREMINDER_TIMES_SENT, 2);
        map.put(EventKeys.PILLREMINDER_RETRY_INTERVAL, 5);
        MotechEvent motechEvent = new MotechEvent("subject", map);

        listener.handlePillReminderEvent(motechEvent);

        verify(pillReminderCall).execute("patientId", "dosageId", 2, 4, 5);
    }
}