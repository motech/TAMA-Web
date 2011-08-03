package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;

import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;

public class MessageForAdherenceWhenPreviousDosageCapturedCommandTest {

    @Mock
    HttpSession httpSession;

    @Mock
    PillReminderService pillReminderService;

    @Mock
    DosageAdherenceLogs dosageAdherenceLogs;

    private MessageForAdherenceWhenPreviousDosageCapturedCommand command;
    private IVRRequest ivrRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(pillReminderService, dosageAdherenceLogs);
        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, "r1", PillReminderCall.DOSAGE_ID, "d1"));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        Mockito.when(pillReminderService.getPreviousDosage("r1", "d1")).thenReturn(new DosageResponse(0, 0, "pd1", null) );
        Mockito.when(dosageAdherenceLogs.isPreviousDosageTaken("pd1")).thenReturn(true);
        LocalDate now = DateUtility.today();
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesTotalCount("r1", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(100);
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(75);

        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        assertEquals(String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, 75), message[0]);

        Mockito.verify(pillReminderService).getPreviousDosage("r1", "d1");
        Mockito.verify(dosageAdherenceLogs).isPreviousDosageTaken("pd1");
    }

    @Test
    public void shouldNotReturnAnyMessageWhenPreviousDosageInformationNotCaptured() {
        Mockito.when(pillReminderService.getPreviousDosage("r1", "d1")).thenReturn(new DosageResponse(0, 0, "pd1", null));
        Mockito.when(dosageAdherenceLogs.isPreviousDosageTaken("pd1")).thenReturn(false);

        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        assertEquals(0, message.length);

        Mockito.verify(pillReminderService).getPreviousDosage("r1", "d1");
        Mockito.verify(dosageAdherenceLogs).isPreviousDosageTaken("pd1");
    }

    @Test
    public void shouldReturnAdherencePercentMessageIfPreviousDosageNotPresent() {
        Mockito.when(pillReminderService.getPreviousDosage("r1", "d1")).thenReturn(new DosageResponse(0, 0, null, null));
        LocalDate now = DateUtility.today();
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesTotalCount("r1", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(100);
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(60);

        String[] message = command.execute(new IVRContext(ivrRequest, new IVRSession(httpSession)));
        assertEquals(String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, 60), message[0]);
    }
}