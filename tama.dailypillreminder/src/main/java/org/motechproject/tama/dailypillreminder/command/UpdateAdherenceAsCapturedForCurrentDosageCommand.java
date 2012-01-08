package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceAsCapturedForCurrentDosageCommand extends DailyPillReminderTreeCommand {

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public UpdateAdherenceAsCapturedForCurrentDosageCommand(DailyPillReminderService dailyPillReminderService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(dailyPillReminderService);
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        DosageStatus newStatus = DosageStatus.from(context.dtmfInput());
        PillRegimen pillRegimen = context.pillRegimen();
        dailyReminderAdherenceService.recordDosageAdherenceAsCaptured(context.patientId(),
                pillRegimen.getId(),
                context.currentDose(),
                newStatus,
                context.callStartTime());

        return new String[0];
    }
}
