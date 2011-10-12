package org.motechproject.tama.web.command;

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
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageForAdherenceWhenPreviousDosageNotCapturedCommandTest {
    @Mock
    IVRSession ivrSession;
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;

    private MessageForAdherenceWhenPreviousDosageNotCapturedCommand command;
    @Mock
    private IVRRequest ivrRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        String REGIMEN_ID = "regimenId";
        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");

        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 4, 12, 0));
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 8, 4));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 5), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 5, 15, 5, 0));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2011, 8, 4, 12, 0));


        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        Mockito.when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
        command = new MessageForAdherenceWhenPreviousDosageNotCapturedCommand(allDosageAdherenceLogs, new TamaIVRMessage(null));
    }

    @Test
    public void shouldReturnAdherenceMessageWhenPreviousDosageInformationNotCaptured() {
        when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(56);

        String[] message = command.execute(new IVRContext(ivrRequest, ivrSession));
        assertEquals(3, message.length);
        assertEquals(TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, message[0]);
        assertEquals("Num_100", message[1]);
        assertEquals(TamaIVRMessage.PERCENT, message[2]);
    }
}