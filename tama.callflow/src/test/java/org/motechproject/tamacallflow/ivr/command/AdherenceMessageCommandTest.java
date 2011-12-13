package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.util.DateUtil;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceMessageCommandTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private AdherenceMessageCommand adherenceMessageCommand;
    private TAMAIVRContextForTest tamaIvrContext;
    private PillRegimenResponse pillRegimenResponse;
    private DateTime callStartTime;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(DateUtil.class);
        TamaIVRMessage tamaIvrMessage = new TamaIVRMessage(null);
        adherenceMessageCommand = new AdherenceMessageCommand(null, tamaIvrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        DosageResponse currentDosage = pillRegimenResponse.getDosages().get(1);
        callStartTime = DateUtil.newDateTime(currentDosage.getStartDate(), currentDosage.getDosageHour(), currentDosage.getDosageMinute(), 0);
        tamaIvrContext = new TAMAIVRContextForTest().patientId("patient_id").pillRegimen(pillRegimenResponse).callDirection(CallDirection.Outbound).callStartTime(callStartTime).dosageId(currentDosage.getDosageId());
    }

    @Test
    public void shouldReportAdherenceAsOfLastRecordedDose() {
        when(dailyReminderAdherenceService.getAdherenceInPercentage("patient_id", callStartTime)).thenReturn(25.0);
        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_025", TamaIVRMessage.PERCENT}, adherenceMessageCommand.executeCommand(tamaIvrContext));
    }
}
