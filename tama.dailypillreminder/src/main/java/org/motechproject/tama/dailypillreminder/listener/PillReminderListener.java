package org.motechproject.tama.dailypillreminder.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.dailypillreminder.call.PillReminderCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PillReminderListener {
    private PillReminderCall call;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public PillReminderListener(PillReminderCall call) {
        this.call = call;
    }

    @MotechListener(subjects = EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT)
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        try {
            Map<String, Object> parameters = motechEvent.getParameters();
            String patientDocId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
            String dosageId = (String) parameters.get(EventKeys.DOSAGE_ID_KEY);
            int timesAlreadyCalled = (Integer) parameters.get(EventKeys.PILLREMINDER_TIMES_SENT);
            int totalCallsToBeMade = (Integer) parameters.get(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND);
            int retryInterval = (Integer) parameters.get(EventKeys.PILLREMINDER_RETRY_INTERVAL);
            call.execute(patientDocId, dosageId, timesAlreadyCalled, totalCallsToBeMade, retryInterval);
        } catch (Exception e) {
            logger.error("Failed to handle PillReminderCall event, this event would not be retried but the subsequent repeats would happen.", e);
        }
    }
}
