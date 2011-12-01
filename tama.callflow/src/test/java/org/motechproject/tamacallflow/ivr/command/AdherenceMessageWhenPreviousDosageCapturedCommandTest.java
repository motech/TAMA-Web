package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class AdherenceMessageWhenPreviousDosageCapturedCommandTest {

    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private TAMAIVRContextForTest ivrContext;
    private AdherenceMessageWhenPreviousDosageCapturedCommand command;

    @Before
    public void setup() {
        initMocks(this);
        command = new AdherenceMessageWhenPreviousDosageCapturedCommand(allDosageAdherenceLogs, new TamaIVRMessage(null), null, dailyReminderAdherenceTrendService);
    }

    @Test
    public void shouldPlayAdherenceMessageWhenPreviousDosageIsRecorded() {
        DateTime now = new DateTime(2011, 8, 4, 12, 0);
        LocalDate today = now.toLocalDate();
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 1), null, today, null),
                new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), null, today, null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "p1", 0, 0, dosageResponses);
        ivrContext = new TAMAIVRContextForTest().patientId("p1").dosageId("currentDosageId").pillRegimen(pillRegimenResponse).callStartTime(now).callDirection(CallDirection.Outbound);

        when(dailyReminderAdherenceTrendService.getAdherence("p1")).thenReturn(1.0);

        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_100", TamaIVRMessage.PERCENT}, command.executeCommand(ivrContext));
    }

    @Test
    public void shouldNotReturnAnyMessagesWhenPreviousDosageInformationIsNotCaptured() {
        DateTime now = new DateTime(2011, 8, 16, 5, 0);
        LocalDate yesterady = now.toLocalDate().minusDays(1);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("previousDosageId", new Time(8, 0), DateUtil.newDate(2011, 7, 1), null, yesterady, null),
                new DosageResponse("currentDosageId", new Time(16, 0), DateUtil.newDate(2011, 7, 1), null, yesterady, null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "p1", 0, 0, dosageResponses);
        ivrContext = new TAMAIVRContextForTest().patientId("p1").dosageId("currentDosageId").pillRegimen(pillRegimenResponse).callStartTime(now).callDirection(CallDirection.Outbound);
        assertEquals(0, command.executeCommand(ivrContext).length);
    }
}