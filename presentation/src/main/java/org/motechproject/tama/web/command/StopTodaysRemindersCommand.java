package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.DosageResponseWithDate;
import org.motechproject.tama.ivr.TAMAIVRContext;
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
        DosageResponseWithDate currentDosage = pillRegimenSnapshot(ivrContext).getCurrentDosage();
        pillReminderService.dosageStatusKnown(pillRegimen(ivrContext).getPillRegimenId(), currentDosage.getDosageId(), currentDosage.getDosageDate());
        return new String[0];
    }

}


