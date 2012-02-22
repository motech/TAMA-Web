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
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PlayAdherenceTrendFeedbackCommandTest {

    PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private DateTime dateTime = DateUtil.now();

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        mockStatic(DateUtil.class);
        dateTime = new DateTime();
        setUpDate();
        playAdherenceTrendFeedbackCommand = new PlayAdherenceTrendFeedbackCommand(dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    private void setUpDate() {
        when(DateUtil.now()).thenReturn(dateTime);
    }

    @Test
    public void testExecute() throws NoAdherenceRecordedException {
        String externalId = "someExternalId";
        String[] result;

        when(dailyReminderAdherenceService.getAdherencePercentage(externalId, dateTime)).thenReturn(80.0);
        when(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf(externalId, DateUtil.now())).thenReturn(true);

        result = playAdherenceTrendFeedbackCommand.execute(externalId);

        Assert.assertEquals(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING, result[0]);
    }
}
