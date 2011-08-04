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
import static org.mockito.Matchers.any;

public class MessageForAdherenceWhenPreviousDosageCapturedCommandTest {
    @Mock
    IVRSession ivrSession;
    @Mock
    DosageAdherenceLogs dosageAdherenceLogs;

    private MessageForAdherenceWhenPreviousDosageCapturedCommand command;
    private IVRRequest ivrRequest;

    private final String REGIMEN_ID = "regimenId";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, REGIMEN_ID, PillReminderCall.DOSAGE_ID, "currentDosageId"));

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), DateUtility.newDate(2011, 7, 1), DateUtility.newDate(2012, 7, 1), DateUtility.now(), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), DateUtility.newDate(2011, 7, 5), DateUtility.newDate(2012, 7, 5), DateUtility.now(), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(dosageAdherenceLogs, DateUtility.newLocalDate(2011, 8, 4));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        Date lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, lastTakenDate, new ArrayList<MedicineResponse>()));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));
        LocalDate now = DateUtility.today();
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(REGIMEN_ID, now, DateUtility.addDaysToLocalDate(now, -28))).thenReturn(75);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(IVRMessage.ADHERENCE_PERCENT_MESSAGE, message[0]);
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

    @Test
    public void shouldCalculateAdherencePercentage() {
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(45);

        int adherencePercentage = command.getAdherencePercentage(REGIMEN_ID, DateUtility.today(), 56);
        assertEquals(80, adherencePercentage);
    }

}