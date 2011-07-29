package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.CurrentDosageMenuAction;
import org.motechproject.tama.ivr.action.pillreminder.DosageMenuAction;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageMenuAction;

import static org.mockito.Mockito.*;

public class DosageMenuActionTest extends BaseActionTest {
    @Mock
    private CurrentDosageMenuAction currentDosageMenuAction;
    @Mock
    private PreviousDosageMenuAction previousDosageMenuAction;
    private DosageMenuAction action;

    @Before
    public void setUp() {
        super.setUp();
        action = new DosageMenuAction(currentDosageMenuAction, previousDosageMenuAction);
    }

    @Test
    public void shouldForwardToCurrentDosageMenuAction() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_DOSE_RESPONSE);

        action.handle(ivrRequest, request, response);

        verify(currentDosageMenuAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldForwardToPreviousDosageMenuAction() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALL_STATE)).thenReturn(IVRCallState.COLLECT_PREVIOUS_DOSE_RESPONSE);

        action.handle(ivrRequest, request, response);

        verify(previousDosageMenuAction).handle(ivrRequest, request, response);
    }
}

