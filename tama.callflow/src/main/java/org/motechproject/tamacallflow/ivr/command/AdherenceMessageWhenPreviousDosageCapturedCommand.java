package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageWhenPreviousDosageCapturedCommand extends AdherenceMessageCommand {
    @Autowired
    public AdherenceMessageWhenPreviousDosageCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, PillReminderService pillReminderService, DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        super(allDosageAdherenceLogs, ivrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(tamaivrContext);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return getAdherenceMessage(tamaivrContext);
        }
        return new String[0];
    }
}