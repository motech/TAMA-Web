package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPreviousPillReminderCommand extends DailyPillReminderTreeCommand {
    @Autowired
    public StopPreviousPillReminderCommand(DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        PillRegimen pillRegimen = context.pillRegimen();
        Dose previousDose = context.previousDose();
        dailyPillReminderService.setLastCapturedDate(pillRegimen.getId(), previousDose.getDosageId(), previousDose.getDate());
        return new String[0];
    }
}
