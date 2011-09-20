package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageOnPillTakenTest {

    IVRContext context;
    IVRRequest request;
    MessageOnPillTaken messageOnPillTaken;

    @Before
    public void setup() {
        messageOnPillTaken = new MessageOnPillTaken();
        context = mock(IVRContext.class);
        request = mock(IVRRequest.class);
        when(context.ivrRequest()).thenReturn(request);
    }

    @Test
    public void shouldReturnDoseTakenMessageForTheFirstReminder() {
        when(request.getParameter(PillReminderCall.TIMES_SENT)).thenReturn("0");
        String[] messages = messageOnPillTaken.execute(context);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.DOSE_TAKEN_ON_TIME, messages[0]);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[1]);
    }

    @Test
    public void shouldNotReturnDoseTakenMessageForSubsequentReminders() {
        when(request.getParameter(PillReminderCall.TIMES_SENT)).thenReturn("2");
        String[] messages = messageOnPillTaken.execute(context);
        assertEquals(1, messages.length);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[0]);
    }
}
