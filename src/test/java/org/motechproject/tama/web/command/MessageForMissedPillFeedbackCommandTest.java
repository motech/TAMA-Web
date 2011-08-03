package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageForMissedPillFeedbackCommandTest {
    @Mock
    HttpSession httpSession;

    @Mock
    DosageAdherenceLogs dosageAdherenceLogs;

    private MessageForMissedPillFeedbackCommand command;
    private IVRRequest ivrRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        command = new MessageForMissedPillFeedbackCommand(dosageAdherenceLogs);
        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, "r1", PillReminderCall.DOSAGE_ID, "d1"));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheFirstTime() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount("r1")).thenReturn(1);
        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME, message[0]);
        verify(dosageAdherenceLogs).findScheduledDosagesFailureCount("r1");
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheSecondTime() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount("r1")).thenReturn(2);
        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceMoreThan90() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount("r1")).thenReturn(5);
        LocalDate today = DateUtility.today();
        when(dosageAdherenceLogs.findScheduledDosagesTotalCount("r1", today,DateUtility.addDaysToLocalDate(today,-28))).thenReturn(100);
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", today, DateUtility.addDaysToLocalDate(today, -28))).thenReturn(91);
        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceBetween70And90() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount("r1")).thenReturn(5);
        LocalDate today = DateUtility.today();
        when(dosageAdherenceLogs.findScheduledDosagesTotalCount("r1", today,DateUtility.addDaysToLocalDate(today,-28))).thenReturn(100);
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", today, DateUtility.addDaysToLocalDate(today, -28))).thenReturn(90);
        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceLessThan70() {
        when(dosageAdherenceLogs.findScheduledDosagesFailureCount("r1")).thenReturn(5);
        LocalDate today = DateUtility.today();
        when(dosageAdherenceLogs.findScheduledDosagesTotalCount("r1", today,DateUtility.addDaysToLocalDate(today,-28))).thenReturn(100);
        when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", today, DateUtility.addDaysToLocalDate(today, -28))).thenReturn(69);
        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(IVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70, message[0]);
    }
}