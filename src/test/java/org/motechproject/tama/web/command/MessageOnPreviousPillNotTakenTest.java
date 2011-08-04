package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageOnPreviousPillNotTakenTest {
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest request;
    @Mock
    private PillReminderService pillReminderService;
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillNotTaken = new MessageOnPreviousPillNotTaken(pillReminderService);
        when(context.ivrRequest()).thenReturn(request);
    }

    @Test
    public void shouldReturnPillNotTakenMessage() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        params.put(PillReminderCall.PREVIOUS_DOSAGE_ID, "previousDosageId");
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("medicine1", null, null));
        medicineResponses.add(new MedicineResponse("medicine2", null, null));
        DosageResponse previousDosageResponse = new DosageResponse("previousDosageId", new Time(10, 05), null, null, null, medicineResponses);

        when(request.getTamaParams()).thenReturn(params);
        when(pillReminderService.getPreviousDosage("regimenId", "currentDosageId")).thenReturn(previousDosageResponse);

        String[] messages = messageOnPreviousPillNotTaken.execute(context);
        assertEquals(4, messages.length);
        assertEquals(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE, messages[0]);
        assertEquals(IVRMessage.MORNING, messages[1]);
        assertEquals(IVRMessage.DOSE, messages[2]);
        assertEquals(IVRMessage.TRY_NOT_TO_MISS, messages[3]);
    }
}
