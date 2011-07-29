package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageTakenAction;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PreviousDosageTakenActionTest extends BaseActionTest {
    @Mock
    private PillReminderService service;
    private PreviousDosageTakenAction action;
    public static final String REGIMEN_ID = "regimenId";
    public static final String DOSAGE_ID = "dosageId";

    @Before
    public void setUp() {
        super.setUp();
        action = new PreviousDosageTakenAction(service, messages, audits);
    }

    @Test
    public void shouldCallPillReminderServiceToUpdateDosageStatus() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        Map params = new HashMap();
        params.put(PillReminderCall.REGIMEN_ID, REGIMEN_ID);
        params.put(PillReminderCall.DOSAGE_ID, DOSAGE_ID);
        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(messages.getWav(IVRMessage.YOU_SAID_YOU_TOOK)).thenReturn("you_said_you_took");
        when(messages.getWav(IVRMessage.YESTERDAYS)).thenReturn("yesterdays");
        when(messages.getWav(IVRMessage.EVENING)).thenReturn("evening");
        when(messages.getWav(IVRMessage.DOSE)).thenReturn("dose");

        String responseXML = action.handle(ivrRequest, request, response);

        verify(service).updateDosageTaken(REGIMEN_ID, DOSAGE_ID);
        assertEquals("<response>" +
                     "<playaudio>you_said_you_took</playaudio>" +
                     "<playaudio>yesterdays</playaudio>" +
                     "<playaudio>evening</playaudio>" +
                     "<playaudio>dose</playaudio>" +
                     "<hangup/></response>", sanitize(responseXML));
    }
}

