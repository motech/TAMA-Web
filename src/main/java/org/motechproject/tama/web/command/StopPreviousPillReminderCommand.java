package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPreviousPillReminderCommand extends StopTodaysRemindersCommand {
    @Autowired
    public StopPreviousPillReminderCommand(PillReminderService pillReminderService) {
        super(pillReminderService);
    }

    @Override
    protected String getDosageId(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).getPreviousDosage().getDosageId();
    }
}
