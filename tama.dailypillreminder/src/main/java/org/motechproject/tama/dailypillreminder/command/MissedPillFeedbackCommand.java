package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MissedPillFeedbackCommand extends AdherenceMessageCommand {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MissedPillFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService, DailyPillReminderService dailyPillReminderService) {
        super(allDosageAdherenceLogs, ivrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService, dailyPillReminderService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        PillRegimen pillRegimen = context.pillRegimen();
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
                int adherencePercentage = 0;
                try {
                    adherencePercentage = (int) (dailyReminderAdherenceService.getAdherencePercentage(context.patientDocumentId(), context.callStartTime()));
                } catch (NoAdherenceRecordedException ignored) {
                    logger.info("No Adherence records found!");
                }
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