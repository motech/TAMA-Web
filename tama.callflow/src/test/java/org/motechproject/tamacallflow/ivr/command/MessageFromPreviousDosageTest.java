package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageFromPreviousDosageTest {
    private MessageFromPreviousDosage messageFromPreviousDosage;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        messageFromPreviousDosage = new MessageFromPreviousDosage(null);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        context = new TAMAIVRContextForTest().dosageId("currentDosageId").callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).callDirection(CallDirection.Outbound);
    }

    @Test
    public void shouldReturnMessagesWhenPreviousDosageHasNotBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageLastTakenDate = DateUtil.today().minusDays(2);
        ArrayList<MedicineResponse> medicines = new ArrayList<MedicineResponse>();
        medicines.add(new MedicineResponse("medicine1", DateUtil.today().minusDays(1), null));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, dosageLastTakenDate, medicines));

        context.pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        List<String> messages = Arrays.asList(messageFromPreviousDosage.executeCommand(context));
        assertTrue(messages.contains(TamaIVRMessage.YOUR));
        assertTrue(messages.contains(TamaIVRMessage.YESTERDAYS));
        assertTrue(messages.contains(TamaIVRMessage.DOSE_NOT_RECORDED));
        assertTrue(messages.contains(TamaIVRMessage.YESTERDAY));
        assertTrue(messages.contains(TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE));
        assertTrue(messages.contains("pillmedicine1"));
        assertTrue(messages.contains(TamaIVRMessage.FROM_THE_BOTTLE));
        assertTrue(messages.contains(TamaIVRMessage.PREVIOUS_DOSE_MENU));
    }

    @Test
    public void shouldReturnNoMessagesWhenPreviousDosageHasBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, lastTakenDate, new ArrayList<MedicineResponse>()));

        context.pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        String[] messages = messageFromPreviousDosage.executeCommand(context);

        assertEquals(0, messages.length);
    }
}
