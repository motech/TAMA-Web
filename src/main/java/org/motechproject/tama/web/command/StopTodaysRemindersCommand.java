package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopTodaysRemindersCommand extends BaseTreeCommand {
    private PillReminderService pillReminderService;

    @Autowired
    public StopTodaysRemindersCommand(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        pillReminderService.stopTodaysReminders(getRegimenIdFrom(ivrContext), getDosageId(ivrContext));

        return new String[0];
    }

    protected String getDosageId(IVRContext ivrContext) {
        return getDosageIdFrom(ivrContext);
    }
}
