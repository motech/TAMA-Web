package org.motechproject.tama.ivr.outbound;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.ivr.action.PillReminderAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutboundCallService {

    private PillReminderAction action;
    @Autowired
    public OutboundCallService(PillReminderAction action) {
      this.action = action;
    }

    @MotechListener(subjects = "org.motechproject.server.pillreminder.scheduler-reminder")
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        action.execute((String) motechEvent.getParameters().get("ExternalID"));
    }
}
