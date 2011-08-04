package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.*;

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
    private MessageFromPreviousDosage messageFromPreviousDosage;
    private Map params = new HashMap<String, String>();
    private ArrayList<MedicineResponse> medicineResponses;

    @Before
    public void setup() {
        initMocks(this);

        messageFromPreviousDosage = new MessageFromPreviousDosage();
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        when(ivrRequest.getTamaParams()).thenReturn(params);

        medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("medicine1", null, null));
        medicineResponses.add(new MedicineResponse("medicine2", null, null));
    }

    @Test
    public void shouldReturnMessagesWhenPreviousDosageHasNotBeenTaken() {
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());

        List<String> messages = Arrays.asList(messageFromPreviousDosage.execute(context));
        assertTrue(messages.contains(IVRMessage.MORNING));
        assertTrue(messages.contains(IVRMessage.IN_THE_MORNING));
        assertTrue(messages.contains("medicine3"));
    }

    @Test
    public void shouldReturnNoMessagesWhenPreviousDosageHasBeenTaken() {
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        pillRegimenResponse.getDosages().remove(1);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        String[] messages = messageFromPreviousDosage.execute(context);

        assertEquals(0, messages.length);
    }
}
