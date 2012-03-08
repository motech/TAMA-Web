package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayAdherenceTrendFeedbackCommand extends AdherenceCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    protected DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public PlayAdherenceTrendFeedbackCommand(DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService,
                                             DailyPillReminderAdherenceService dailyReminderAdherenceService,
                                             DailyPillReminderService dailyPillReminderService) {

        super(dailyPillReminderService, dailyReminderAdherenceService);
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    public String[] execute(String patientId) {
        ArrayList<String> result = new ArrayList<String>();
        DateTime now = DateUtil.now();

        result.add(TamaIVRMessage.YOUR_ADHERENCE_IS);
        result.addAll(super.adherenceMessage(patientId));
        result.addAll(adherenceTrendMessage(patientId, adherencePercentage(patientId, now), now));
        return result.toArray(new String[result.size()]);
    }

    protected List<String> adherenceTrendMessage(String patientId,
                                                 double adherencePercentageAsOfNow,
                                                 DateTime now) {
        List<String> result = new ArrayList<String>();
        boolean falling = dailyReminderAdherenceTrendService.isAdherenceFallingAsOf(patientId, now);

        if (adherencePercentageAsOfNow > 90) {
            goodAdherence(result);
        } else if (adherencePercentageAsOfNow > 70) {
            averageAdherence(result, falling);
        } else {
            poorAdherence(result, falling);
        }
        return result;
    }

    private double adherencePercentage(String patientId, DateTime now) {
        double adherencePercentageAsOfNow;
        try {
            adherencePercentageAsOfNow = dailyReminderAdherenceService.getAdherencePercentage(patientId, now);
        } catch (NoAdherenceRecordedException e) {
            adherencePercentageAsOfNow = 0.00;
            logger.info("No Adherence records found!");
        }
        return adherencePercentageAsOfNow;
    }

    private void averageAdherence(List<String> result, boolean falling) {
        if (falling) {
            moreRegularly(result);
        } else {
            isImproving(result);
        }
    }

    private void poorAdherence(List<String> result, boolean falling) {
        if (falling) {
            needToImproveSubstantially(result);
        } else {
            improveFurther(result);
        }
    }

    private void improveFurther(List<String> result) {
        // A message indicating my adherence is improving but it
        // needs to improve further is played
        result.add(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING);
    }

    private void needToImproveSubstantially(List<String> result) {
        // A message indicating my adherence needs to improve
        // substantially is played
        result.add(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING);
    }

    private void isImproving(List<String> result) {
        // A message indicating my adherence is improving but it can
        // improve further is played
        result.add(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
    }

    private void moreRegularly(List<String> result) {
        // A message saying my adherence can improve and I need to
        // take my doses more regularly should be played to me
        result.add(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING);
    }

    private void goodAdherence(List<String> result) {
        // A message saying indicating that I’ve done well and should
        // try not to miss a single dose is played to me
        result.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
    }
}