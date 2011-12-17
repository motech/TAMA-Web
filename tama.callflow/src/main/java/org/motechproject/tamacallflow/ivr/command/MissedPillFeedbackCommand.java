package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MissedPillFeedbackCommand extends AdherenceMessageCommand {

    @Autowired
    public MissedPillFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        super(allDosageAdherenceLogs, ivrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse(ivrContext));
        int dosagesTaken = allDosageAdherenceLogs.getDosageTakenCount(pillRegimen.getId());
        int dosagesNotTaken = pillRegimen.getNumberOfDosesAsOf(ivrContext.callStartTime()) - dosagesTaken;
        switch (dosagesNotTaken) {
            case 1:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME};
            case 2:
            case 3:
            case 4:
                return new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME};
            default:
                int adherencePercentage = (int) (dailyReminderAdherenceService.getAdherenceInPercentage(ivrContext.patientId(), ivrContext.callStartTime()));
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