package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillTakenCommand extends BaseTreeCommand {
    private PillReminderService pillReminderService;

    @Autowired
    public PillTakenCommand(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        pillReminderService.updateDosageTaken(getRegimenIdFrom(ivrContext), getDosageIdFrom(ivrContext));
        return new String[0];
    }
}
