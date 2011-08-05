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
    private DosageResponse currentDosage;
    private DosageResponse previousDosage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ivrRequest = new IVRRequest();
        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", PillReminderCall.REGIMEN_ID, REGIMEN_ID, PillReminderCall.DOSAGE_ID, "currentDosageId"));

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtility.newDate(2011, 7, 1), DateUtility.newDate(2012, 7, 1), DateUtility.now(), medicineResponses);
        dosageResponses.add(currentDosage);
        previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtility.newDate(2011, 7, 5), DateUtility.newDate(2012, 7, 5), DateUtility.now(), medicineResponses);
        dosageResponses.add(previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(dosageAdherenceLogs, DateUtility.newLocalDate(2011, 8, 4));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(56);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(2, message.length);
        assertEquals(IVRMessage.ADHERENCE_PERCENT_MESSAGE, message[0]);
        assertEquals("100", message[1]);
    }

    @Test
    public void shouldReturnAdherenceMessageForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        Date lastTakenDate = null;
        currentDosage = new DosageResponse("currentDosageId", new Time(10, 5), DateUtility.newDate(2011, 7, 1), DateUtility.newDate(2012, 7, 1), lastTakenDate, new ArrayList<MedicineResponse>());
        dosages.add(currentDosage);

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));
        Mockito.when(dosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(28);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(2, message.length);
        assertEquals(IVRMessage.ADHERENCE_PERCENT_MESSAGE, message[0]);
        assertEquals("100", message[1]);
    }

    @Test
    public void shouldNotReturnAnyMessageWhenPreviousDosageInformationNotCapturedYesterday() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        Date lastTakenDate = new DateTime().minusDays(2).toDate();
        currentDosage = new DosageResponse("currentDosageId", new Time(10, 5), DateUtility.newDate(2011, 7, 1), DateUtility.newDate(2012, 7, 1), lastTakenDate, new ArrayList<MedicineResponse>());
        dosages.add(currentDosage);

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