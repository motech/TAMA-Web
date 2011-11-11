package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTaken extends BaseTreeCommand {
    @Autowired
    public MessageOnPillTaken(PillReminderService pillReminderService) {
        super(pillReminderService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        if (ivrContext.numberOfTimesReminderSent() == 0) {
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);
        }
        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[messages.size()]);
    }
}