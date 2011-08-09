package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;

import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
    }

    @Test
    public void shouldUpdateDosageDateAsTodayOnPillTaken_TAMACallsPatient() {
        DosageResponse previousDosage = pillRegimenResponse.getDosages().get(0);
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, currentDosage.getDosageId());

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(previousDosage.getDosageHour()));

        previousPillTakenCommand.execute(context);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), previousDosage.getDosageId(), DateUtil.today());
    }

    @Test
    public void shouldUpdateDosageDateAsYesterdayOnPillTaken_TAMACallsPatient() {
        DosageResponse previousDosage = pillRegimenResponse.getDosages().get(0);
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, currentDosage.getDosageId());

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(previousDosage.getDosageHour() - (pillRegimenResponse.getReminderRepeatWindowInHours() + 1)));

        previousPillTakenCommand.execute(context);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), previousDosage.getDosageId(), DateUtil.today().minusDays(1));
    }


    @Test
    public void shouldUpdateDosageDateOnPillTaken_PatientCallsTAMA() {

        when(ivrRequest.hasNoTamaData()).thenReturn(true);
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(22, 5), null, null, null, null);
        List<DosageResponse> dosages = Arrays.asList(dosageResponse);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosageResponse.getDosageHour() - (pillRegimenResponse.getReminderRepeatWindowInHours() + 1)));

        previousPillTakenCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), "currentDosageId", DateUtil.today().minusDays(1));
    }
}
