package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopTodaysRemindersCommand extends BaseTreeCommand {
    @Autowired
    public StopTodaysRemindersCommand(PillReminderService pillReminderService) {
        super(pillReminderService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        Dose currentDose = pillRegimenSnapshot(ivrContext).getCurrentDose();
        pillReminderService.dosageStatusKnown(pillRegimenResponse(ivrContext).getPillRegimenId(), currentDose.getDosageId(), currentDose.getDate());
        return new String[0];
    }

}


