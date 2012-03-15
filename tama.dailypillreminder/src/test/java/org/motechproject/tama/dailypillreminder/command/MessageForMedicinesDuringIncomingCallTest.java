package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;


public class MessageForMedicinesDuringIncomingCallTest {
    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;

    private LocalDate today;

    private DateTime now;

    private DailyPillReminderContextForTest context;

    private void setUpTime() {
        today = DateUtil.today();
        now = DateUtil.now();
    }

    private void setUpContexts() {
        TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest().patientDocumentId("patientId");
        context = new DailyPillReminderContextForTest(tamaivrContextForTest).pillRegimen(new PillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()));
    }

    @Before
    public void setup() {
        initMocks(this);

        messageForMedicinesDuringIncomingCall = new MessageForMedicinesDuringIncomingCall(null);
        setUpContexts();
        setUpTime();
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeWithinDosagePillWindow() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);
        assertArrayEquals(new String[]{TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillmedicine1", "pillmedicine2", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW}, messages);
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeAfterDosagePillWindow() {
        int dosageHour = 10;
        DateTime timeAfterPillWindow = now.withHourOfDay(dosageHour + 3).withMinuteOfHour(5);
        context.callStartTime(timeAfterPillWindow);
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(dosageHour, 5), today.minusDays(2),
                today.plusDays(1), today.minusDays(1),
                Arrays.asList(new MedicineResponse("medicine3", today, today))));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        context.pillRegimen(new PillRegimen(pillRegimenResponse)).callDirection(CallDirection.Outbound);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);
        assertArrayEquals(new String[]{TamaIVRMessage.NOT_REPORTED_IF_TAKEN, "pillmedicine3", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_AFTER_PILL_WINDOW}, messages);
    }
}
