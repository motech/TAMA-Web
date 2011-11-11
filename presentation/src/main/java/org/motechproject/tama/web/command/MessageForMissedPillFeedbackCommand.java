package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageForMissedPillFeedbackCommand extends DosageAdherenceCommand {
    @Autowired
    public MessageForMissedPillFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(allDosageAdherenceLogs, ivrMessage, pillReminderService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        PillRegimenResponse pillRegimenResponse = pillRegimen(ivrContext);
        int dosagesFailureCount = allDosageAdherenceLogs.findScheduledDosagesFailureCount(pillRegimenResponse.getPillRegimenId());

        switch (dosagesFailureCount) {
            case 1:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME};
            case 2:
            case 3:
            case 4:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME};
            default:
                int adherencePercentage = getAdherencePercentage(pillRegimenResponse.getPillRegimenId(),
                        pillRegimenSnapshot(ivrContext).getScheduledDosagesTotalCountForLastFourWeeks());
                return new String[]{getMissedPillFeedbackMessageFor(adherencePercentage)};
        }
    }

    private String getMissedPillFeedbackMessageFor(int adherencePercentage) {
        if (adherencePercentage > 90)
            return TamaIVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90;
        else if (adherencePercentage >= 70 && adherencePercentage <= 90)
            return TamaIVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90;

        return TamaIVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70;
    }
}