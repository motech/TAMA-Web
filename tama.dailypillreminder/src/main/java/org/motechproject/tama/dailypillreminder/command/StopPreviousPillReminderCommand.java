package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPreviousPillReminderCommand extends DailyPillReminderTreeCommand {
    @Autowired
    public StopPreviousPillReminderCommand(PillReminderService pillReminderService) {
        super(pillReminderService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        Dose currentDose = pillRegimenSnapshot(context).getPreviousDose();
        pillReminderService.dosageStatusKnown(pillRegimenResponse(context).getPillRegimenId(), currentDose.getDosageId(), currentDose.getDate());
        return new String[0];
    }
}
