package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
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
    IVRSession ivrSession;
    @Mock
    DosageAdherenceLogs dosageAdherenceLogs;

    private MessageForAdherenceWhenPreviousDosageCapturedCommand command;
    private IVRRequest ivrRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(dosageAdherenceLogs);
        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, "regimenId", PillReminderCall.DOSAGE_ID, "currentDosageId"));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        Mockito.when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        Mockito.when(dosageAdherenceLogs.isPreviousDosageTaken("previousDosageId")).thenReturn(true);
        LocalDate now = DateUtility.today();
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesTotalCount("regimenId", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(100);
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("regimenId", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(75);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, 75), message[0]);

        Mockito.verify(dosageAdherenceLogs).isPreviousDosageTaken("previousDosageId");
    }

    @Test
    public void shouldNotReturnAnyMessageWhenPreviousDosageInformationNotCaptured() {
        Mockito.when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        Mockito.when(dosageAdherenceLogs.isPreviousDosageTaken("previousDosageId")).thenReturn(false);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(0, message.length);

        Mockito.verify(dosageAdherenceLogs).isPreviousDosageTaken("previousDosageId");
    }
}