package org.motechproject.tama.messages.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class MessageHistoryTest {

    @Test
    public void shouldSetTheMessageReadOnDate() {
        DateTime now = DateUtil.now();

        MessageHistory history = new MessageHistory();
        history.readOn(now);
        assertEquals(now, history.getLastPlayedOn());
    }

    @Test
    public void shouldIncrementTheNumberOfTimesPlayedWhenMessageIsRead() {
        DateTime now = DateUtil.now();

        MessageHistory history = new MessageHistory();

        assertEquals(0, history.getCount());
        history.readOn(now);
        assertEquals(1, history.getCount());
    }
}
