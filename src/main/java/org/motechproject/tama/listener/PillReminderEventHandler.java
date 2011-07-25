package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PillReminderEventHandler {

    private PillReminderCall action;
    @Autowired
    public PillReminderEventHandler(PillReminderCall action) {
      this.action = action;
    }

    @MotechListener(subjects = "org.motechproject.server.pillreminder.scheduler-reminder")
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        action.execute((String) motechEvent.getParameters().get("ExternalID"));
    }
}
