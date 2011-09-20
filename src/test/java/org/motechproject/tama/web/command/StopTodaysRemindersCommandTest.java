package org.motechproject.tama.web.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

    }

    @Test
    public void shouldUpdateDosageDateOnPillTaken() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(currentDosage.getDosageHour()));

        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn(currentDosage.getDosageId());
        stopTodaysRemindersCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today());
    }

    @Test
    public void shouldUpdateDosageDateToYesterdayForYesterdaysDosage() {
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(2);
        DosageResponse nextDosage = pillRegimenResponse.getDosages().get(0);
        DateTime timeSoThatCurrentDosageIsYesterdaysDosage = DateUtil.now().withHourOfDay(nextDosage.getDosageHour()).minusHours(pillRegimenResponse.getReminderRepeatWindowInHours() + 1);
        when(ivrSession.getCallTime()).thenReturn(timeSoThatCurrentDosageIsYesterdaysDosage);

        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        stopTodaysRemindersCommand.execute(context);

        verify(pillReminderService).dosageStatusKnown("regimenId", currentDosage.getDosageId(), DateUtil.today().minusDays(1));
    }
}
