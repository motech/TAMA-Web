package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
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
        PillRegimen pillRegimen = pillRegimen(context);
        Dose currentDose = pillRegimen.getPreviousDoseAt(context.callStartTime());
        pillReminderService.dosageStatusKnown(pillRegimen.getId(), currentDose.getDosageId(), currentDose.getDate());
        return new String[0];
    }
}
