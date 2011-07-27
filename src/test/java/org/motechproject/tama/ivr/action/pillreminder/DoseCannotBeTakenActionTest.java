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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DoseCannotBeTakenActionTest extends BaseActionTest {

    private DoseCannotBeTakenAction action;
    @Mock
    private DoNotHavePillsAction doNotHavePillsAction;
    @Mock
    private PillReminderService service;

    @Before
    public void setUp() {
        super.setUp();
        when(doNotHavePillsAction.getKey()).thenReturn("2");
        action = new DoseCannotBeTakenAction(messages, doNotHavePillsAction);
    }

    @Test
    public void shouldAdvicePatientWhenHeDoesNotCarryPills(){
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(ivrRequest.getData()).thenReturn("2%23");

        action.handle(ivrRequest, request, response);

        verify(doNotHavePillsAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldPlayMenuAskingWhyTheDoseCannotBeTaken(){
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(ivrRequest.getData()).thenReturn("99%23");
        when(messages.getWav(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU)).thenReturn("y_u_no_take_dose");

        String responseXML = action.handle(ivrRequest, request, response);

        assertEquals("<response><collectdtmf><playaudio>y_u_no_take_dose</playaudio></collectdtmf></response>", sanitize(responseXML));
    }

}
