package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
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
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.TIMES_SENT, 0);
        when(request.getTamaParams()).thenReturn(params);
        assertArrayEquals(new String[]{IVRMessage.DOSE_TAKEN}, messageOnPillTaken.execute(context));
    }

    @Test
    public void shouldNotReturnDoseTakenMessageForSubsequentReminders() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.TIMES_SENT, 2);
        when(request.getTamaParams()).thenReturn(params);
        assertArrayEquals(new String[0], messageOnPillTaken.execute(context));
    }
}
