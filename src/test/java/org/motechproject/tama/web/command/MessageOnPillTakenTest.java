package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;

import static junit.framework.Assert.assertEquals;

public class MessageOnPillTakenTest {
    MessageOnPillTaken messageOnPillTaken;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        messageOnPillTaken = new MessageOnPillTaken(null);
        context = new TAMAIVRContextForTest();
    }

    @Test
    public void shouldReturnDoseTakenMessageForTheFirstReminder() {
        context.numberOfTimesReminderSent(0);
        String[] messages = messageOnPillTaken.executeCommand(context);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.DOSE_TAKEN_ON_TIME, messages[0]);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[1]);
    }

    @Test
    public void shouldNotReturnDoseTakenMessageForSubsequentReminders() {
        context.numberOfTimesReminderSent(2);
        String[] messages = messageOnPillTaken.executeCommand(context);
        assertEquals(1, messages.length);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[0]);
    }
}
