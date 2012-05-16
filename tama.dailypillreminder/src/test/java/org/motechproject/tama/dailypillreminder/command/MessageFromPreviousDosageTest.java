package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.MedicineResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageFromPreviousDosageTest {
    private MessageFromPreviousDosage messageFromPreviousDosage;
    private DailyPillReminderContextForTest context;
    public LocalDate today;

    @Before
    public void setup() {
        messageFromPreviousDosage = new MessageFromPreviousDosage(null);
        today = DateUtil.today();
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).callStartTime(DateUtil.newDateTime(today, 16, 0, 0)).callDirection(CallDirection.Outbound);
    }

    @Test
    public void shouldReturnMessagesWhenPreviousDosageHasNotBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageLastTakenDate = today.minusDays(2);
        ArrayList<MedicineResponse> medicines = new ArrayList<MedicineResponse>();
        medicines.add(new MedicineResponse("medicine1", today.minusDays(1), null));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), today.minusDays(2), null, dosageLastTakenDate, medicines));

        context.pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, 5, dosages));

        List<String> messages = Arrays.asList(messageFromPreviousDosage.executeCommand(context));
        assertTrue(messages.contains(TamaIVRMessage.YOUR));
        assertTrue(messages.contains(TamaIVRMessage.YESTERDAYS));
        assertTrue(messages.contains(TamaIVRMessage.DOSE_NOT_RECORDED));
        assertTrue(messages.contains(TamaIVRMessage.YESTERDAY));
        assertTrue(messages.contains(TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE));
        assertTrue(messages.contains("pillmedicine1"));
        assertTrue(messages.contains(TamaIVRMessage.FROM_THE_BOTTLE_FOR_PREVIOUS_DOSAGE));
        assertTrue(messages.contains(TamaIVRMessage.PREVIOUS_DOSE_MENU));
    }

    @Test
    public void shouldReturnNoMessagesWhenPreviousDosageHasBeenTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), today, null, lastTakenDate, new ArrayList<MedicineResponse>()));

        context.pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, 5, dosages));

        String[] messages = messageFromPreviousDosage.executeCommand(context);
        assertEquals(0, messages.length);
    }
}
