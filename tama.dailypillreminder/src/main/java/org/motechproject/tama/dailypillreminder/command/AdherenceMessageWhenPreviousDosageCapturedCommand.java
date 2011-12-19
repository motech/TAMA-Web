package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageWhenPreviousDosageCapturedCommand extends AdherenceMessageCommand {
    @Autowired
    public AdherenceMessageWhenPreviousDosageCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, PillReminderService pillReminderService, DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(allDosageAdherenceLogs, ivrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(context);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return getAdherenceMessage(context);
        }
        return new String[0];
    }
}