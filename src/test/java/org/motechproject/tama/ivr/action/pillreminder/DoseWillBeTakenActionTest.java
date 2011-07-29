package org.motechproject.tama.ivr.action.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DoseWillBeTakenActionTest extends BaseActionTest {

    private DoseWillBeTakenAction action;
    @Mock
    private PillReminderService service;

    @Before
    public void setUp() {
        super.setUp();
        action = new DoseWillBeTakenAction(service, messages, audits);
    }

    @Test
    public void shouldPlayPleaseTakeYourDrugAudio() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);
        when(messages.getWav(IVRMessage.PLEASE_TAKE_DOSE)).thenReturn("please_take_dose");
        when(messages.getWav(IVRMessage.MINUTES)).thenReturn("minutes");
        when(messages.getWav(IVRMessage.PILL_REMINDER_RETRY_INTERVAL)).thenReturn("15");

        String responseXML = action.handle(ivrRequest, request, response);

        assertEquals("<response>" +
                "<playaudio>please_take_dose</playaudio>" +
                "<playaudio>15</playaudio>" +
                "<playaudio>minutes</playaudio>" +
                "<hangup/>" +
                "</response>", sanitize(responseXML));
    }
}
