package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePreviousPillAdherenceCommand extends UpdateAdherenceCommand {

    @Autowired
    public UpdatePreviousPillAdherenceCommand(PillReminderService pillReminderService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(pillReminderService, dailyReminderAdherenceService);
    }

    @Override
    protected String getDosageId(DailyPillReminderContext context) {
        return pillRegimenSnapshot(context).getPreviousDose().getDosageId();
    }

    @Override
    protected Dose getDose(DailyPillReminderContext context) {
        return pillRegimenSnapshot(context).getPreviousDose();
    }
}
