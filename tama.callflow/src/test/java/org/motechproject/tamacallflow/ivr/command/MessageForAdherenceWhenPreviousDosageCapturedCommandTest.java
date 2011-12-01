package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageForAdherenceWhenPreviousDosageCapturedCommandTest {
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;
    private MessageForAdherenceWhenPreviousDosageCapturedCommand command;
    private final String REGIMEN_ID = "regimenId";
    private DosageResponse currentDosage;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), medicineResponses);
        dosageResponses.add(currentDosage);
        DosageResponse previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), DateUtil.today(), medicineResponses);
        dosageResponses.add(previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(allDosageAdherenceLogs, new TamaIVRMessage(null), null);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 4, 12, 0));
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 7, 1));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 10, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 10, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 5), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 5, 15, 5, 0));

        ivrContext = new TAMAIVRContextForTest().dosageId("currentDosageId").pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2011, 8, 4, 12, 0));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(56);
        ivrContext.callDirection(CallDirection.Outbound);
        String[] message = command.executeCommand(ivrContext);
        assertEquals(3, message.length);
        assertEquals(TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, message[0]);
        assertEquals("Num_100", message[1]);
        assertEquals(TamaIVRMessage.PERCENT, message[2]);
    }

    @Test
    public void shouldReturnAdherenceMessageForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;

        currentDosage = new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), DateUtil.today().plusYears(1), lastTakenDate, new ArrayList<MedicineResponse>());
        dosages.add(currentDosage);

        ivrContext.callStartTime(new DateTime(2011, 7, 1, 12, 0)).pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages)).callDirection(CallDirection.Inbound);
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

        String[] message = command.executeCommand(ivrContext);
        assertEquals(3, message.length);
        assertEquals(TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, message[0]);
        assertEquals("Num_100", message[1]);
        assertEquals(TamaIVRMessage.PERCENT, message[2]);
    }

    @Test
    public void shouldNotReturnAnyMessageWhenPreviousDosageInformationNotCapturedYesterday() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = DateUtil.today().minusDays(2);
        currentDosage = new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), DateUtil.today().plusYears(1), lastTakenDate, new ArrayList<MedicineResponse>());
        dosages.add(currentDosage);

        ivrContext.pillRegimen(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages)).callDirection(CallDirection.Inbound);

        String[] message = command.executeCommand(ivrContext);
        assertEquals(0, message.length);
    }

    @Test
    public void shouldCalculateAdherencePercentage() {
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(45);

        int adherencePercentage = command.getAdherencePercentage(REGIMEN_ID, 56);
        assertEquals(80, adherencePercentage);
    }

    @Test
    public void shouldCalculateSuccessfulCountForExactlyTwentyEightDays() {
        mockStatic(DateUtil.class);
        LocalDate toDate = new LocalDate(2011, 8, 18);
        when(DateUtil.today()).thenReturn(toDate);

        command.getAdherencePercentage(REGIMEN_ID, 28);

        verify(allDosageAdherenceLogs).findScheduledDosagesSuccessCount(any(String.class), eq(new LocalDate(2011, 7, 21)), eq(toDate));
    }
}