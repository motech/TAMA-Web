package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageWhenPreviousDosageCapturedCommand extends AdherenceMessageCommand {

    @Autowired
    public AdherenceMessageWhenPreviousDosageCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, DailyPillReminderService dailyPillReminderService,
                                                             DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(allDosageAdherenceLogs, dailyReminderAdherenceTrendService, dailyReminderAdherenceService, dailyPillReminderService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (context.isPreviousDoseTaken()) {
            return getAdherenceMessage(context);
        }
        return new String[0];
    }
}