package org.motechproject.tamacallflow.ivr.command;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.OutboxContextForTest;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
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
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private OutboxContextForTest context;
    private DateTime dateTime = DateTime.now();

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        mockStatic(DateUtil.class);
         dateTime = new DateTime();
        setUpDate();
        playAdherenceTrendFeedbackCommand = new PlayAdherenceTrendFeedbackCommand(dailyReminderAdherenceTrendService);
        context = new OutboxContextForTest();

    }

    private void setUpDate(){
        when(DateUtil.now()).thenReturn(dateTime);
    }

    @Test
    public void testExecute() {
        String externalId = "someExternalId";
        String[] result;

        context.partyId(externalId);
        when(dailyReminderAdherenceTrendService.getAdherence(externalId)).thenReturn(0.8);
        when(dailyReminderAdherenceTrendService.isAdherenceFalling(externalId)).thenReturn(true);

        result = playAdherenceTrendFeedbackCommand.execute(context);

        Assert.assertEquals(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING, result[0]);

    }
}
