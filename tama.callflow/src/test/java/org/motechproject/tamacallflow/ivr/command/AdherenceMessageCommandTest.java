package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceMessageCommandTest {

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private AdherenceMessageCommand adherenceMessageCommand;

    @Before
    public void setup() {
        initMocks(this);
        TamaIVRMessage tamaIvrMessage = new TamaIVRMessage(null);
        adherenceMessageCommand = new AdherenceMessageCommand(null, tamaIvrMessage, dailyReminderAdherenceTrendService);
    }

    @Test
    public void shouldBuildAdherenceMessage() {
        DateTime now = new DateTime(2011, 11, 22, 10, 30);
        TAMAIVRContextForTest tamaIvrContext = new TAMAIVRContextForTest().patientId("patient_id").callStartTime(now);
        when(dailyReminderAdherenceTrendService.getAdherence("patient_id")).thenReturn(0.25);
        assertArrayEquals(new String[]{TamaIVRMessage.YOUR_ADHERENCE_IS_NOW, "Num_025", TamaIVRMessage.PERCENT}, adherenceMessageCommand.executeCommand(tamaIvrContext));
    }
}
