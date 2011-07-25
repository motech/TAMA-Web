package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PillReminderListener {
    private PillReminderCall call;

    @Autowired
    public PillReminderListener(PillReminderCall call) {
        this.call = call;
    }

    @MotechListener(subjects = "org.motechproject.server.pillreminder.scheduler-reminder")
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        String patientDocId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        String dosageId = (String) motechEvent.getParameters().get(EventKeys.DOSAGE_ID_KEY);
        call.execute(patientDocId, dosageId);
    }
}
