package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class StopTodaysRemindersCommandTest {
    @Mock
    private PillReminderService pillReminderService;
    private StopTodaysRemindersCommand stopTodaysRemindersCommand;
    private PillRegimenResponse pillRegimenResponse;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setup() {
        initMocks(this);
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new TAMAIVRContextForTest().pillRegimen(pillRegimenResponse).callDirection(CallDirection.Outbound);
        stopTodaysRemindersCommand = new StopTodaysRemindersCommand(pillReminderService);
    }

    @Test
    public void shouldUpdateDosageDateOnPillTaken() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);
        ivrContext.callStartTime(DateUtil.now().withHourOfDay(currentDosage.getDosageHour())).dosageId(currentDosage.getDosageId());
        stopTodaysRemindersCommand.executeCommand(ivrContext);
        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today());
    }

    @Test
    public void shouldUpdateDosageDateToYesterdayForYesterdaysDosage() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(2);
        DosageResponse nextDosage = pillRegimenResponse.getDosages().get(0);
        DateTime timeSoThatCurrentDosageIsYesterdaysDosage = DateUtil.now().withHourOfDay(nextDosage.getDosageHour()).minusHours(pillRegimenResponse.getReminderRepeatWindowInHours() + 1);
        ivrContext.callStartTime(timeSoThatCurrentDosageIsYesterdaysDosage).callDirection(CallDirection.Inbound);
        stopTodaysRemindersCommand.executeCommand(ivrContext);
        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today().minusDays(1));
    }
}
