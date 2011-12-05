package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(DateUtil.class)
public class AdherenceMessageCommandTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private AdherenceMessageCommand adherenceMessageCommand;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(DateUtil.class);
        TamaIVRMessage tamaIvrMessage = new TamaIVRMessage(null);
        adherenceMessageCommand = new AdherenceMessageCommand(null, tamaIvrMessage, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Test
    public void shouldReportAdherenceAsOfLastRecordedDose() {
        TAMAIVRContextForTest tamaIvrContext = new TAMAIVRContextForTest().patientId("patient_id");
        when(dailyReminderAdherenceService.getAdherenceAsOfLastRecordedDose("patient_id")).thenReturn(0.25);
        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_025", TamaIVRMessage.PERCENT}, adherenceMessageCommand.executeCommand(tamaIvrContext));
    }
}
