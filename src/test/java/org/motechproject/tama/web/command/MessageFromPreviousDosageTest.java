package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageFromPreviousDosageTest {

    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private PillReminderService pillReminderService;
    private MessageFromPreviousDosage messageFromPreviousDosage;
    private Map params = new HashMap<String, String>();;

    @Before
    public void setup() {
        initMocks(this);

        messageFromPreviousDosage = new MessageFromPreviousDosage(pillReminderService);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        when(ivrRequest.getTamaParams()).thenReturn(params);
    }

    @Test
    public void shouldAddPreviousDosageIdToRequestParams() {
        DosageResponse previousDosageResponse = new DosageResponse(10, 05, "previousDosageId", Arrays.asList("medicine1", "medicine2"));
        when(pillReminderService.getPreviousDosage("regimenId", "currentDosageId")).thenReturn(previousDosageResponse);

        messageFromPreviousDosage.execute(context);

        assertTrue(params.containsKey(PillReminderCall.PREVIOUS_DOSAGE_ID));
        assertEquals("previousDosageId", params.get(PillReminderCall.PREVIOUS_DOSAGE_ID));
    }

    @Test
    public void shouldReturnMessagesWhenPreviousDosageHasNotBeenTaken() {
        DosageResponse previousDosageResponse = new DosageResponse(10, 05, "previousDosageId", Arrays.asList("medicine1", "medicine2"));
        when(pillReminderService.getPreviousDosage("regimenId", "currentDosageId")).thenReturn(previousDosageResponse);

        List<String> messages = Arrays.asList(messageFromPreviousDosage.execute(context));
        assertTrue(messages.contains(IVRMessage.MORNING));
        assertTrue(messages.contains(IVRMessage.IN_THE_MORNING));
        assertTrue(messages.contains("medicine1"));
        assertTrue(messages.contains("medicine2"));
    }

    @Test
    public void shouldReturnNoMessagesWhenPreviousDosageHasBeenTaken() {
        DosageResponse previousDosageResponse = new DosageResponse(10, 05, "currentDosageId", Arrays.asList("medicine1", "medicine2"));
        when(pillReminderService.getPreviousDosage("regimenId", "currentDosageId")).thenReturn(previousDosageResponse);

        String[] messages = messageFromPreviousDosage.execute(context);

        assertEquals(0, messages.length);
    }
}
