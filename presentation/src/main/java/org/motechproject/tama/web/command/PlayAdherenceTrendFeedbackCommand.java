package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.outbox.OutboxContext;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
/* applicable only when patient is on daily call */
public class PlayAdherenceTrendFeedbackCommand {
    AllDosageAdherenceLogs allDosageAdherenceLogs;
    PillReminderService pillReminderService;

    @Autowired
    public PlayAdherenceTrendFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, PillReminderService pillReminderService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
    }

    public String[] execute(OutboxContext outboxContext) {
        ArrayList<String> result = new ArrayList<String>();
        String patientId = outboxContext.partyId();
        DateTime now = DateUtil.now();
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patientId);

        double adherencePercentageAsOfNow = getAdherencePercentage(pillRegimen, now);
        double adherencePercentageAsOfLastWeek = getAdherencePercentage(pillRegimen, now.minusWeeks(1));
        boolean falling = adherencePercentageAsOfNow < adherencePercentageAsOfLastWeek;

        if (adherencePercentageAsOfNow > 0.9) {
            // A message saying indicating that I’ve done well and should
            // try not to miss a single dose is played to me
            result.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
        } else if (adherencePercentageAsOfNow > 0.7) {
            if (falling) {
                // A message saying my adherence can improve and I need to
                // take my doses more regularly should be played to me
                result.add(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING);
            } else {
                // A message indicating my adherence is improving but it can
                // improve further is played
                result.add(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
            }
        } else {
            if (falling) {
                // A message indicating my adherence needs to improve
                // substantially is played
                result.add(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING);
            } else {
                // A message indicating my adherence is improving but it
                // needs to improve further is played
                result.add(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private double getAdherencePercentage(PillRegimenResponse pillRegimen, DateTime asOfDate) {
        String regimenId = pillRegimen.getPillRegimenId();
        int scheduledDosagesTotalCountForLastFourWeeksAsOfNow = getScheduledDosagesTotalCount(pillRegimen, asOfDate.minusWeeks(4), asOfDate);
        int dosagesTakenForLastFourWeeksAsOfNow = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId,
                asOfDate.minusWeeks(4).toLocalDate(), asOfDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeksAsOfNow) / scheduledDosagesTotalCountForLastFourWeeksAsOfNow;
    }

    public int getScheduledDosagesTotalCount(PillRegimenResponse pillRegimen, DateTime startDate, DateTime endDate) {
        return DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(startDate, endDate, pillRegimen);
    }
}