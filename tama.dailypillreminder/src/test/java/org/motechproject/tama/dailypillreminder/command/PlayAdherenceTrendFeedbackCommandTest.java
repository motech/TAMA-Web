package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class PlayAdherenceTrendFeedbackCommandTest extends BaseUnitTest {

    PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    @Mock
    private DailyPillReminderService dailyPillReminderService;
    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private DateTime dateTime = DateUtil.now();

    @Before
    public void setUp() throws NoSuchFieldException {
        initMocks(this);
        dateTime = new DateTime();
        setUpDate();

        playAdherenceTrendFeedbackCommand = new PlayAdherenceTrendFeedbackCommand(
                dailyReminderAdherenceTrendService,
                dailyReminderAdherenceService,
                dailyPillReminderService
        );
    }

    private void setUpDate() {
        mockCurrentDate(dateTime);
    }

    @Test
    public void testExecute() throws NoAdherenceRecordedException {
        String externalId = "someExternalId";
        String[] result;

        when(dailyReminderAdherenceService.getAdherencePercentage(externalId, dateTime)).thenReturn(80.0);
        when(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf(externalId, DateUtil.now())).thenReturn(true);

        result = playAdherenceTrendFeedbackCommand.execute(externalId);

        assertArrayEquals(
                new String[]{
                        "M02_01_adherence1",
                        "Num_080",
                        "001_06_03_HasBecomePercent",
                        TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING
                },
                result
        );
    }
}
