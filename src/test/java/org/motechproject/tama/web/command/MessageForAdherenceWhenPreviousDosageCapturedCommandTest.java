package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class MessageForAdherenceWhenPreviousDosageCapturedCommandTest {
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
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        Date lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, lastTakenDate, new ArrayList<MedicineResponse>()));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));
        LocalDate now = DateUtility.today();
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesTotalCount("regimenId", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(100);
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount("regimenId", now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(75);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, 75), message[0]);
    }

    @Test
    public void shouldNotReturnAnyMessageWhenPreviousDosageInformationNotCaptured() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        DateTime dosageLastTakenDate = new DateTime().minusDays(2);
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, dosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(0, message.length);
    }
}