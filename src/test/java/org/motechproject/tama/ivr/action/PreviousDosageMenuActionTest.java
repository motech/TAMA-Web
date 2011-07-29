package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageMenuAction;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageNotTakenAction;
import org.motechproject.tama.ivr.action.pillreminder.PreviousDosageTakenAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PreviousDosageMenuActionTest extends BaseActionTest {
    private PreviousDosageMenuAction action;
    @Mock
    private PreviousDosageTakenAction previousDoseTakenAction;
    @Mock
    private PreviousDosageNotTakenAction previousDoseNotTakenAction;

    @Before
    public void setUp() {
        super.setUp();
        when(previousDoseTakenAction.getKey()).thenReturn("1");
        when(previousDoseNotTakenAction.getKey()).thenReturn("3");
        action = new PreviousDosageMenuAction(messages, audits, previousDoseTakenAction, previousDoseNotTakenAction);
    }

    @Test
    public void shouldAskPreviousDoseTakenActionToHandleIfCallStateIsDoseResponseOfValueEqualToOne() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("1%23");

        action.handle(ivrRequest, request, response);

        verify(previousDoseTakenAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldAskPreviousDoseNotTakenActionToHandleIfCallStateIsDoseResponseOfValueEqualToThree() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("3%23");

        action.handle(ivrRequest, request, response);

        verify(previousDoseNotTakenAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldAskPreviousDoseTakenActionToHandleIfCallStateIsDoseResponseIsUnknown() {
        IVRRequest ivrRequest = mock(IVRRequest.class);

        when(ivrRequest.getData()).thenReturn("99%23");
        when(messages.getWav(IVRMessage.PREVIOUS_DOSE_MENU)).thenReturn("menu");

        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("<response><collectdtmf><playaudio>menu</playaudio></collectdtmf></response>", sanitize(responseXML));
    }
}

