package org.motechproject.tama.ivr.action.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DoseTakenActionTest extends BaseActionTest {
    public static final String REGIMEN_ID = "regimenId";
    public static final String DOSAGE_ID = "dosageId";

    private DoseTakenAction action;
    @Mock
    private PillReminderService service;

    @Before
    public void setUp() {
        super.setUp();
        action = new DoseTakenAction(service, audits, messages);
    }

    @Test
    public void shouldCallPillReminderServiceToUpdateDosageStatus() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        Map params = new HashMap();
        params.put(PillReminderCall.REGIMEN_ID, REGIMEN_ID);
        params.put(PillReminderCall.DOSAGE_ID, DOSAGE_ID);
        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(messages.getWav(IVRMessage.DOSE_TAKEN)).thenReturn("dose_taken");

        String responseXML = action.handle(ivrRequest, request, response);

        verify(service).updateDosageTaken(REGIMEN_ID, DOSAGE_ID);
        assertEquals("<response><playaudio>dose_taken</playaudio><hangup/></response>", sanitize(responseXML));
    }
}
