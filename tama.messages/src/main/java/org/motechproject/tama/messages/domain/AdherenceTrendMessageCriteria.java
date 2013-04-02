package org.motechproject.tama.messages.domain;

import org.joda.time.DateTime;

import static org.joda.time.Days.daysBetween;

public class AdherenceTrendMessageCriteria {

    public boolean shouldPlay(double adherencePercentage, MessageHistory messageHistory, DateTime dateTime) {
        if (messageHistory.neverPlayed()) {
            return true;
        } else {
            int lastPlayed = daysBetween(messageHistory.getLastPlayedOn(), dateTime).getDays();
            return (adherencePercentage > 90) ? (lastPlayed >= 10) : (lastPlayed >= 6);
        }
    }
}
