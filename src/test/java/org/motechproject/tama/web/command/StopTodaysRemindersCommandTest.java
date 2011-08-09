package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StopTodaysRemindersCommandTest {

    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private PillReminderService pillReminderService;
    private StopTodaysRemindersCommand stopTodaysRemindersCommand;
    private PillRegimenResponse pillRegimenResponse;

    @Before
    public void setup() {
        initMocks(this);

        stopTodaysRemindersCommand = new StopTodaysRemindersCommand(pillReminderService);
        context = new IVRContext(ivrRequest, ivrSession);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

    }

    @Test
    public void shouldUpdateDosageDateOnPillTaken() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, currentDosage.getDosageId());
        when(ivrRequest.getTamaParams()).thenReturn(params);

        stopTodaysRemindersCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today());
    }

    @Test
    public void shouldUpdateDosageDateToYesterdayForYesterdaysDosage() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);
        DateTime timeSoThatCurrentDosageIsYesterdaysDosage = DateUtil.now().withHourOfDay(currentDosage.getDosageHour()).minusHours(pillRegimenResponse.getReminderRepeatWindowInHours() + 1);
        when(ivrSession.getCallTime()).thenReturn(timeSoThatCurrentDosageIsYesterdaysDosage);

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, currentDosage.getDosageId());
        when(ivrRequest.getTamaParams()).thenReturn(params);

        stopTodaysRemindersCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today().minusDays(1));
    }
}
