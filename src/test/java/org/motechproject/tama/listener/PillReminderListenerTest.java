package org.motechproject.tama.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class PillReminderListenerTest {
    private PillReminderListener listener;
    @Mock
    private PillReminderCall pillReminderCall;
    private MotechEvent motechEvent;

    @Before
    public void setUp() {
        initMocks(this);
        listener = new PillReminderListener(pillReminderCall);
    }

    @Test
    public void shouldExecutePillReminderCallWithPatientIdAndDosageId() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EventKeys.EXTERNAL_ID_KEY, "patientId");
        map.put(EventKeys.PILLREMINDER_ID_KEY, "regimenId");
        map.put(EventKeys.DOSAGE_ID_KEY, "dosageId");
        map.put(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND, 4);
        map.put(EventKeys.PILLREMINDER_TIMES_SENT, 2);
        MotechEvent motechEvent = new MotechEvent("subject", map);

        listener.handlePillReminderEvent(motechEvent);

        verify(pillReminderCall).execute("patientId", "regimenId", "dosageId");
    }

    @Test
    public void shouldExecuteLastPillReminderCallWhenNoMoreCallsWillBeMade() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EventKeys.EXTERNAL_ID_KEY, "patientId");
        map.put(EventKeys.PILLREMINDER_ID_KEY, "regimenId");
        map.put(EventKeys.DOSAGE_ID_KEY, "dosageId");
        map.put(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND, 4);
        map.put(EventKeys.PILLREMINDER_TIMES_SENT, 3);
        MotechEvent motechEvent = new MotechEvent("subject", map);

        listener.handlePillReminderEvent(motechEvent);

        verify(pillReminderCall).executeLastCall("patientId", "regimenId", "dosageId");
    }
}