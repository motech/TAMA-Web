package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceAsCapturedForCurrentDosageCommand extends DailyPillReminderTreeCommand {

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public UpdateAdherenceAsCapturedForCurrentDosageCommand(PillReminderService pillReminderService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(pillReminderService);
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        DosageStatus newStatus = DosageStatus.from(context.dtmfInput());
        PillRegimen pillRegimen = pillRegimen(context);
        dailyReminderAdherenceService.recordDosageAdherenceAsCaptured(context.patientId(),
                pillRegimen.getId(),
                pillRegimen.getDoseAt(context.callStartTime()),
                newStatus,
                context.callStartTime());

        return new String[0];
    }
}
