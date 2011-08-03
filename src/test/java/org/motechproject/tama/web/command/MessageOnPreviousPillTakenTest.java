package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageOnPreviousPillTakenTest {
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest request;
    @Mock
    private PillReminderService pillReminderService;
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillTaken = new MessageOnPreviousPillTaken(pillReminderService);
        when(context.ivrRequest()).thenReturn(request);
    }

    @Test
    public void shouldReturnPillTakenMessage() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        params.put(PillReminderCall.PREVIOUS_DOSAGE_ID, "previousDosageId");
        DosageResponse previousDosageResponse = new DosageResponse(10, 05, "previousDosageId", Arrays.asList("medicine1", "medicine2"));

        when(request.getTamaParams()).thenReturn(params);
        when(pillReminderService.getPreviousDosage("regimenId", "currentDosageId")).thenReturn(previousDosageResponse);

        String[] messages = messageOnPreviousPillTaken.execute(context);
        assertEquals(3, messages.length);
        assertEquals(IVRMessage.YOU_SAID_YOU_TOOK, messages[0]);
        assertEquals(IVRMessage.MORNING, messages[1]);
        assertEquals(IVRMessage.DOSE, messages[2]);
    }
}
