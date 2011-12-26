package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PlayAdherenceTrendFeedbackCommand {

    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public PlayAdherenceTrendFeedbackCommand(DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    public String[] execute(String patientId) {
        ArrayList<String> result = new ArrayList<String>();
        DateTime now = DateUtil.now();

        double adherencePercentageAsOfNow = dailyReminderAdherenceService.getAdherencePercentage(patientId, now);
        boolean falling = dailyReminderAdherenceTrendService.isAdherenceFallingAsOf(patientId, DateUtil.now());

        if (adherencePercentageAsOfNow > 90) {
            // A message saying indicating that I’ve done well and should
            // try not to miss a single dose is played to me
            result.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
        } else if (adherencePercentageAsOfNow > 70) {
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
}