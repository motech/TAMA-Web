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
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;

import static org.mockito.Mockito.*;

public class PillReminderActionTest extends BaseActionTest {

    private PillReminderAction action;
    @Mock
    private IVRMessage messages;
    @Mock
    private Patients patients;
    @Mock
    private Clinics clinics;
    @Mock
    private IVRCallAudits audits;
    @Mock
    private PillReminderService service;
    @Mock
    private DoseNotTakenAction doseNotTakenAction;
    @Mock
    private DoseTakenAction doseTakenAction;
    @Mock
    private DoseWillBeTakenAction doseWillBeTakenAction;
    @Mock
    private DoseRemindAction doseRemindAction;

    @Before
    public void setUp() {
        super.setUp();
        action = new PillReminderAction(messages, patients, clinics, audits, service,
                doseNotTakenAction, doseTakenAction, doseWillBeTakenAction, doseRemindAction);
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

}

