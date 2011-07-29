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
import static org.mockito.MockitoAnnotations.initMocks;

public class DoNotHavePillsActionTest extends BaseActionTest {

    private DoNotHavePillsAction action;
    @Mock
    private PillReminderService service;

    @Before
    public void setUp() {
        initMocks(this);
        action = new DoNotHavePillsAction(service, messages);
    }

    @Test
    public void shouldPlayAdviceForPatientNotCarryingPills() {
        when(request.getSession(false)).thenReturn(session);
        when(messages.getWav(IVRMessage.PLEASE_CARRY_SMALL_BOX)).thenReturn("carry_box");
        IVRRequest ivrRequest = mock(IVRRequest.class);

        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("<response><playaudio>carry_box</playaudio><hangup/></response>", sanitize(responseXML));
    }
}
