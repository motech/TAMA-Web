package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PillReminderListener {
    private PillReminderCall call;

    @Autowired
    public PillReminderListener(PillReminderCall call) {
        this.call = call;
    }

    @MotechListener(subjects = EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT)
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String patientDocId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        String regimenId = (String) parameters.get(EventKeys.PILLREMINDER_ID_KEY);
        String dosageId = (String) parameters.get(EventKeys.DOSAGE_ID_KEY);
        int timesAlreadyCalled = (Integer) parameters.get(EventKeys.PILLREMINDER_TIMES_SENT);
        int totalCallsToBeMade = (Integer) parameters.get(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND);
        call.execute(patientDocId, dosageId, timesAlreadyCalled, totalCallsToBeMade);
    }
}
