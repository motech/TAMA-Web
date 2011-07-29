package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageNotTakenAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreviousDosageNotTakenActionTest extends BaseActionTest {
    private PreviousDosageNotTakenAction action;

    @Before
    public void setUp() {
        super.setUp();
        action = new PreviousDosageNotTakenAction(messages, audits);
    }

    @Test
    public void shouldPlayNotTakenMessageAndHangup() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(messages.getWav(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE)).thenReturn("you_said_you_did_not_take");
        when(messages.getWav(IVRMessage.YESTERDAYS)).thenReturn("yesterdays");
        when(messages.getWav(IVRMessage.EVENING)).thenReturn("evening");
        when(messages.getWav(IVRMessage.DOSE)).thenReturn("dose");
        when(messages.getWav(IVRMessage.TRY_NOT_TO_MISS)).thenReturn("try_not_to_miss");

        String responseXML = action.handle(ivrRequest, request, response);

        assertEquals("<response>" +
                     "<playaudio>you_said_you_did_not_take</playaudio>" +
                     "<playaudio>yesterdays</playaudio>" +
                     "<playaudio>evening</playaudio>" +
                     "<playaudio>dose</playaudio>" +
                     "<playaudio>try_not_to_miss</playaudio>" +
                     "<hangup/></response>", sanitize(responseXML));
    }
}

