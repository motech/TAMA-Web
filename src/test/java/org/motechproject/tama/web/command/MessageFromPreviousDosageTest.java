package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageFromPreviousDosageTest {

    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    private MessageFromPreviousDosage messageFromPreviousDosage;
    private ArrayList<MedicineResponse> medicineResponses;

    @Before
    public void setup() {
        initMocks(this);

        messageFromPreviousDosage = new MessageFromPreviousDosage(new IVRDayMessageBuilder(new TamaIVRMessage(null)));
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);
        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");

        medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("medicine1", null, null));
        medicineResponses.add(new MedicineResponse("medicine2", null, null));

        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 16, 00, 00));
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 00, 00));
    }

    @Test
    public void shouldReturnMessagesWhenPreviousDosageHasNotBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageLastTakenDate = DateUtil.today().minusDays(2);
        ArrayList<MedicineResponse> medicines = new ArrayList<MedicineResponse>();
        medicines.add(new MedicineResponse("medicine1", DateUtil.today().minusDays(1), null));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, dosageLastTakenDate, medicines));

        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        List<String> messages = Arrays.asList(messageFromPreviousDosage.execute(context));
        assertTrue(messages.contains(TamaIVRMessage.MORNING));
        assertTrue(messages.contains(TamaIVRMessage.IN_THE_MORNING));
        assertTrue(messages.contains("pillmedicine1"));
    }

    @Test
    public void shouldReturnNoMessagesWhenPreviousDosageHasBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, lastTakenDate, new ArrayList<MedicineResponse>()));

        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        String[] messages = messageFromPreviousDosage.execute(context);

        assertEquals(0, messages.length);
    }
}
