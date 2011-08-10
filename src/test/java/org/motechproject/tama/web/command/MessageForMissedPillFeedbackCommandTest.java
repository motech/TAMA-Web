package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MessageForMissedPillFeedbackCommandTest {
    @Mock
    IVRSession ivrSession;
    @Mock
    DosageAdherenceLogs dosageAdherenceLogs;

    private MessageForMissedPillFeedbackCommand command;
    private IVRRequest ivrRequest;

    private final String REGIMEN_ID = "r1";
    private final int TOTAL_DOSAGE_COUNT = 56;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), medicineResponses));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), DateUtil.today(), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, REGIMEN_ID, PillReminderCall.DOSAGE_ID, "d1"));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        command = new MessageForMissedPillFeedbackCommand(dosageAdherenceLogs, new DateTime(2011, 8, 4, 12, 0));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheFirstTime() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount(REGIMEN_ID)).thenReturn(1);
        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME, message[0]);
        verify(dosageAdherenceLogs).findScheduledDosagesFailureCount(REGIMEN_ID);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheSecondTime() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount(REGIMEN_ID)).thenReturn(2);
        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceMoreThan90() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount(REGIMEN_ID)).thenReturn(5);
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(TOTAL_DOSAGE_COUNT * 95 / 100);
        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceBetween70And90() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount(REGIMEN_ID)).thenReturn(5);
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(TOTAL_DOSAGE_COUNT * 90 / 100);
        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceLessThan70() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount(REGIMEN_ID)).thenReturn(5);
        LocalDate today = DateUtil.today();
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(REGIMEN_ID, today, today.minusDays(28))).thenReturn(TOTAL_DOSAGE_COUNT * 69 / 100);
        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70, message[0]);
    }
}