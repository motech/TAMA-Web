package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceMessageCommandTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;
    private DailyPillReminderContextForTest tamaIvrContext;
    private PillRegimenResponse pillRegimenResponse;
    private DateTime callStartTime;
    private AdherenceMessageCommand adherenceMessageCommand;

    @Before
    public void setup() {
        initMocks(this);
        DosageResponse currentDosage = setupCurrentDosage();
        setupTimeToDosageTime(currentDosage);
        setupIvrContext(currentDosage);
        TamaIVRMessage tamaIvrMessage = new TamaIVRMessage(null);
        adherenceMessageCommand = new AdherenceMessageCommand(null, tamaIvrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService, null);
    }

    @Test
    public void shouldReportAdherenceAsOfLastRecordedDose() {
        when(dailyReminderAdherenceService.getAdherencePercentage("patient_id", callStartTime)).thenReturn(25.0);
        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_025", TamaIVRMessage.PERCENT}, adherenceMessageCommand.executeCommand(tamaIvrContext));
    }

    private DosageResponse setupCurrentDosage() {
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        return pillRegimenResponse.getDosages().get(1);
    }

    private void setupTimeToDosageTime(DosageResponse currentDosage) {
        callStartTime = DateUtil.newDateTime(currentDosage.getStartDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
    }

    private void setupIvrContext(DosageResponse currentDosage) {
        TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Outbound).callStartTime(callStartTime).patientId("patient_id");
        tamaIvrContext = new DailyPillReminderContextForTest(tamaivrContextForTest).pillRegimen(pillRegimenResponse).dosageId(currentDosage.getDosageId());
    }
}
