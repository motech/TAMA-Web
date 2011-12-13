package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
public class AdherenceMessageWhenPreviousDoseCapturedCommandTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private TAMAIVRContextForTest ivrContext;

    private AdherenceMessageWhenPreviousDosageCapturedCommand command;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() {
        initMocks(this);
        command = new AdherenceMessageWhenPreviousDosageCapturedCommand(allDosageAdherenceLogs, new TamaIVRMessage(null), null, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Test
    public void shouldPlayAdherenceMessageWhenPreviousDosageIsRecorded() {
        DateTime now = new DateTime(2011, 8, 4, 12, 0);
        LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 1), null, today, null),
                new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), null, today, null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "p1", 0, 0, dosageResponses);
        DosageResponse currentDosage = dosageResponses.get(1);
        DateTime callStartTime = DateUtil.newDateTime(currentDosage.getStartDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        ivrContext = new TAMAIVRContextForTest().patientId("p1").dosageId("currentDosageId").pillRegimen(pillRegimenResponse).callStartTime(callStartTime).callDirection(CallDirection.Outbound);

        when(dailyReminderAdherenceService.getAdherenceInPercentage("p1", callStartTime)).thenReturn(100.0);
        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_100", TamaIVRMessage.PERCENT}, command.executeCommand(ivrContext));

    }

    @Test
    public void shouldNotReturnAnyMessagesWhenPreviousDosageInformationIsNotCaptured() {
        DateTime now = new DateTime(2011, 8, 16, 5, 0);
        LocalDate yesterday = now.toLocalDate().minusDays(1);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("previousDosageId", new Time(8, 0), DateUtil.newDate(2011, 7, 1), null, yesterday, null),
                new DosageResponse("currentDosageId", new Time(16, 0), DateUtil.newDate(2011, 7, 1), null, yesterday, null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "p1", 0, 0, dosageResponses);
        ivrContext = new TAMAIVRContextForTest().patientId("p1").dosageId("currentDosageId").pillRegimen(pillRegimenResponse).callStartTime(now).callDirection(CallDirection.Outbound);
        assertEquals(0, command.executeCommand(ivrContext).length);
    }
}