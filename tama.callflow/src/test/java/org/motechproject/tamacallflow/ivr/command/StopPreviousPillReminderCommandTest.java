package org.motechproject.tamacallflow.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class StopPreviousPillReminderCommandTest {
    @Mock
    private PillReminderService pillReminderService;

    private StopPreviousPillReminderCommand previousPillTakenCommand;

    private PillRegimenResponse pillRegimenResponse;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setup() {
        initMocks(this);
        previousPillTakenCommand = new StopPreviousPillReminderCommand(pillReminderService);
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new TAMAIVRContextForTest().pillRegimen(pillRegimenResponse).callDirection(CallDirection.Outbound);
    }

    @Test
    public void shouldUpdateDosageDateAsTodayOnPillTaken_TAMACallsPatient() {
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(Arrays.asList(pillRegimenResponse.getDosages().get(1))).build();
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(0);
        ivrContext.pillRegimen(pillRegimenResponse).dosageId(currentDosage.getDosageId()).callStartTime(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));

        previousPillTakenCommand.executeCommand(ivrContext);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), currentDosage.getDosageId(), DateUtil.today().minusDays(1));
    }

    @Test
    public void shouldUpdateDosageDateAsYesterdayOnPillTaken_TAMACallsPatient() {
        DosageResponse previousDosage = pillRegimenResponse.getDosages().get(2);
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(0);
        ivrContext.dosageId(currentDosage.getDosageId()).callStartTime(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));
        previousPillTakenCommand.executeCommand(ivrContext);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), previousDosage.getDosageId(), DateUtil.today().minusDays(1));
    }

    @Test
    public void shouldUpdateDosageDateOnPillTaken_PatientCallsTAMA() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(22, 5), DateUtil.today(), null, null, null);
        List<DosageResponse> dosages = Arrays.asList(dosageResponse);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        ivrContext.callDirection(CallDirection.Inbound).pillRegimen(pillRegimenResponse).callStartTime(DateUtil.now().withHourOfDay(dosageResponse.getDosageHour()));
        previousPillTakenCommand.executeCommand(ivrContext);
        verify(pillReminderService).dosageStatusKnown(pillRegimenResponse.getPillRegimenId(), "currentDosageId", DateUtil.today().minusDays(1));
    }
}
