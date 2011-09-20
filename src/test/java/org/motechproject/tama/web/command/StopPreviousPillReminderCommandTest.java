package org.motechproject.tama.web.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;

public class StopPreviousPillReminderCommandTest {

    private IVRContext context;

    @Mock
    private IVRRequest ivrRequest;

    @Mock
    private IVRSession ivrSession;

    @Mock
    private PillReminderService pillReminderService;

    private StopPreviousPillReminderCommand previousPillTakenCommand;

    private PillRegimenResponse pillRegimenResponse;

    @Before
    public void setup() {
        initMocks(this);
        previousPillTakenCommand = new StopPreviousPillReminderCommand(pillReminderService);
        context = new IVRContext(ivrRequest, ivrSession);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
    }

    @Test
    public void shouldUpdateDosageDateAsTodayOnPillTaken_TAMACallsPatient() {

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(Arrays.asList(pillRegimenResponse.getDosages().get(1))).build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(0);

        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn(currentDosage.getDosageId());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));

        previousPillTakenCommand.execute(context);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), currentDosage.getDosageId(), DateUtil.today().minusDays(1));
    }

    @Test
    public void shouldUpdateDosageDateAsYesterdayOnPillTaken_TAMACallsPatient() {
        DosageResponse previousDosage = pillRegimenResponse.getDosages().get(2);
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(0);

        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn(currentDosage.getDosageId());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));

        previousPillTakenCommand.execute(context);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), previousDosage.getDosageId(), DateUtil.today().minusDays(1));
    }


    @Test
    public void shouldUpdateDosageDateOnPillTaken_PatientCallsTAMA() {

        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(22, 5), DateUtil.today(), null, null, null);
        List<DosageResponse> dosages = Arrays.asList(dosageResponse);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosageResponse.getDosageHour()));
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        previousPillTakenCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), "currentDosageId", DateUtil.today().minusDays(1));
    }
}
