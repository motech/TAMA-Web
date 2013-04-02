package org.motechproject.tama.messages.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdherenceTrendMessageCriteriaTest {

    private AdherenceTrendMessageCriteria adherenceTrendMessageCriteria;

    @Before
    public void setup() {
        adherenceTrendMessageCriteria = new AdherenceTrendMessageCriteria();
    }

    @Test
    public void shouldPlayAdherenceTrendMessageWhenAdherenceTrendWasNeverPlayed() {
        DateTime now = DateUtil.now();
        MessageHistory history = new MessageHistory();

        history.setLastPlayedOn(null);
        assertTrue(adherenceTrendMessageCriteria.shouldPlay(10d, history, now));
    }

    @Test
    public void testShouldPlayAdherenceTrendMessageWhenAdherenceGreaterThan90() {
        DateTime now = DateUtil.now();
        MessageHistory history = new MessageHistory();

        history.setLastPlayedOn(now.minusDays(10));
        assertTrue(adherenceTrendMessageCriteria.shouldPlay(91d, history, now));
    }

    @Test
    public void testShouldNotPlayAdherenceTrendMessageWhenAdherenceGreaterThan90() {
        DateTime now = DateUtil.now();
        MessageHistory history = new MessageHistory();

        history.setLastPlayedOn(now.minusDays(9));
        assertFalse(adherenceTrendMessageCriteria.shouldPlay(91d, history, now));
    }

    @Test
    public void testShouldPlayAdherenceTrendMessageWhenAdherenceLessThan90() {
        DateTime now = DateUtil.now();
        MessageHistory history = new MessageHistory();

        history.setLastPlayedOn(now.minusDays(6));
        assertTrue(adherenceTrendMessageCriteria.shouldPlay(89d, history, now));
    }

    @Test
    public void testShouldNotPlayAdherenceTrendMessageWhenAdherenceLessThan90() {
        DateTime now = DateUtil.now();
        MessageHistory history = new MessageHistory();

        history.setLastPlayedOn(now.minusDays(5));
        assertFalse(adherenceTrendMessageCriteria.shouldPlay(89d, history, now));
    }
}
