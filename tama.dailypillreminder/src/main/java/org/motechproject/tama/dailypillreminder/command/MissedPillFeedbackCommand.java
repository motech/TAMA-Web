package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MissedPillFeedbackCommand extends AdherenceMessageCommand {

    @Autowired
    public MissedPillFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        super(allDosageAdherenceLogs, ivrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse(context));
        int dosagesTaken = allDosageAdherenceLogs.getDosageTakenCount(pillRegimen.getId());
        int dosagesNotTaken = pillRegimen.getNumberOfDosesAsOf(context.callStartTime()) - dosagesTaken;
        switch (dosagesNotTaken) {
            case 1:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME};
            case 2:
            case 3:
            case 4:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME};
            default:
                int adherencePercentage = (int) (dailyReminderAdherenceService.getAdherenceInPercentage(context.patientId(), context.callStartTime()));
                return new String[]{getMissedPillFeedbackMessageFor(adherencePercentage)};
        }
    }

    private String getMissedPillFeedbackMessageFor(int adherencePercentage) {
        if (adherencePercentage > 90)
            return TamaIVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90;
        else if (adherencePercentage >= 70 && adherencePercentage <= 90)
            return TamaIVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90;
        else
            return TamaIVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70;
    }
}