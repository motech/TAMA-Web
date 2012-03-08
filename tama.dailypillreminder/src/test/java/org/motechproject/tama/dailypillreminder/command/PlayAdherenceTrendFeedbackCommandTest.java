package org.motechproject.tama.dailypillreminder.command;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class PlayAdherenceTrendFeedbackCommandTest extends BaseUnitTest {

    PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private DateTime dateTime = DateUtil.now();

    @Before
    public void setUp() throws NoSuchFieldException {
        initMocks(this);
        dateTime = new DateTime();
        setUpDate();
        playAdherenceTrendFeedbackCommand = new PlayAdherenceTrendFeedbackCommand(dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
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

        assertArrayEquals(new String[]{TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING}, result);
    }
}
