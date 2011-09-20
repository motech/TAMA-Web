package org.motechproject.tama.web.command;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageForAdherenceWhenPreviousDosageCapturedCommandTest {
    @Mock
    IVRSession ivrSession;
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;

    private MessageForAdherenceWhenPreviousDosageCapturedCommand command;
    @Mock
    private IVRRequest ivrRequest;

    private final String REGIMEN_ID = "regimenId";
    private DosageResponse currentDosage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), medicineResponses);
        dosageResponses.add(currentDosage);
        DosageResponse previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), DateUtil.today(), medicineResponses);
        dosageResponses.add(previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        Mockito.when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        command = new MessageForAdherenceWhenPreviousDosageCapturedCommand(allDosageAdherenceLogs, new TamaIVRMessage(null, new FileUtil()));
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 4, 12, 0));
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 7, 1));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 10, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 10, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 5), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 5, 15, 5, 0));
        Mockito.when(ivrSession.getCallTime()).thenReturn(new DateTime(2011, 8, 4, 12, 0));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationCaptured() {
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(56);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(3, message.length);
        assertEquals(TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, message[0]);
        assertEquals("Num_100", message[1]);
        assertEquals(TamaIVRMessage.PERCENT, message[2]);
    }

    @Test
    public void shouldReturnAdherenceMessageForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;

        when(ivrSession.getCallTime()).thenReturn(new DateTime(2011, 7, 1, 12, 0));
        currentDosage = new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), DateUtil.today().plusYears(1), lastTakenDate, new ArrayList<MedicineResponse>());
        dosages.add(currentDosage);

        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
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


        IVRRequest ivrRequest = mock(IVRRequest.class);
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages));

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
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

        verify(allDosageAdherenceLogs).findScheduledDosagesSuccessCount(any(String.class), eq(new LocalDate(2011, 7, 22)), eq(toDate));
    }
}