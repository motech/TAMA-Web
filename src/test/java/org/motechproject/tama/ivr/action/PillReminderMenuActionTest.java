package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.*;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PillReminderMenuActionTest extends BaseActionTest {
    private PillReminderMenuAction action;
    @Mock
    private Patients patients;
    @Mock
    private Clinics clinics;
    @Mock
    private PillReminderService service;
    @Mock
    private DoseCannotBeTakenAction doseCannotBeTakenAction;
    @Mock
    private DoseTakenAction doseTakenAction;
    @Mock
    private DoseWillBeTakenAction doseWillBeTakenAction;
    @Mock
    private DoseRemindAction doseRemindAction;

    @Before
    public void setUp() {
        super.setUp();
        when(doseRemindAction.getKey()).thenReturn("0");
        when(doseTakenAction.getKey()).thenReturn("1");
        when(doseWillBeTakenAction.getKey()).thenReturn("2");
        when(doseCannotBeTakenAction.getKey()).thenReturn("3");
        action = new PillReminderMenuAction(messages, patients, clinics, audits, service,
                doseCannotBeTakenAction, doseTakenAction, doseWillBeTakenAction, doseRemindAction);
    }

    @Test
    public void shouldAskDoseRemindActionToHandleIfCallStateIsNotDoseResponse() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.AUTH_SUCCESS);

        action.handle(ivrRequest, request, response);

        verify(doseRemindAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldAskDoseTakenActionToHandleIfCallStateIsDoseResponseOfValueEqualToOne() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("1%23");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_DOSE_RESPONSE);

        action.handle(ivrRequest, request, response);

        verify(doseTakenAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldAskDoseTakenActionToHandleIfCallStateIsDoseResponseIsUnknown() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("99%23");
        when(request.getSession(false)).thenReturn(session);
        when(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).thenReturn("menu");
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_DOSE_RESPONSE);

        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("<response><collectdtmf><playaudio>menu</playaudio></collectdtmf></response>", sanitize(responseXML));
    }


    @Test
    public void shouldAskDoseCannotBeTakenActionToHandleIfStateIsCollectDoseCannotBeTaken() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("99%23");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_DOSE_CANNOT_BE_TAKEN);
        when(doseCannotBeTakenAction.handle(ivrRequest, request, response)).thenReturn("<response></response>");

        String responseXML = action.handle(ivrRequest, request, response);

        verify(doseCannotBeTakenAction).handle(ivrRequest, request, response);
        assertEquals("<response></response>",responseXML);
    }

}

