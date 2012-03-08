package org.motechproject.tama.dailypillreminder.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceCommandTest extends BaseUnitTest {

    @Mock
    private DailyPillReminderService dailyPillReminderService;
    @Mock
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    private TAMAIVRContextForTest tamaivrContextForTest;
    private AdherenceCommand adherenceCommand;

    @Before
    public void setup() {
        initMocks(this);
        setupIVRContext();
        adherenceCommand = new AdherenceCommand(dailyPillReminderService, dailyPillReminderAdherenceService);
        mockCurrentDate(DateUtil.now());
    }

    private void setupIVRContext() {
        tamaivrContextForTest = new TAMAIVRContextForTest().patientDocumentId("patientId");
    }

    @Test
    public void shouldAddAdherenceValueToMessage() throws NoAdherenceRecordedException {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContextForTest(tamaivrContextForTest);
        when(dailyPillReminderAdherenceService
                .getAdherencePercentage(
                        "patientId",
                        DateUtil.now()
                )
        ).thenReturn(80.0);
        String[] messages = adherenceCommand.executeCommand(dailyPillReminderContext);
        assertEquals("Num_080", messages[0]);
    }

    @Test
    public void shouldAddPercentToMessage() throws NoAdherenceRecordedException {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContextForTest(tamaivrContextForTest);
        when(dailyPillReminderAdherenceService
                .getAdherencePercentage(
                        "patientId",
                        DateUtil.now()
                )
        ).thenReturn(80.0);
        String[] messages = adherenceCommand.executeCommand(dailyPillReminderContext);
        assertEquals("001_06_03_HasBecomePercent", messages[1]);
    }
}
