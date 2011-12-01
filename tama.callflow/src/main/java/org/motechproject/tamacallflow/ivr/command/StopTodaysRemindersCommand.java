package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.Dosage;
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
        Dosage currentDosage = pillRegimenSnapshot(ivrContext).getCurrentDosage();
        pillReminderService.dosageStatusKnown(pillRegimenResponse(ivrContext).getPillRegimenId(), currentDosage.getDosageId(), currentDosage.getDosageDate());
        return new String[0];
    }

}


