package org.motechproject.tama.web.command;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.outbox.OutboxContextForTest;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DosageUtil.class)
public class PlayAdherenceTrendFeedbackCommandTest {

    PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;

    @Mock
    PillReminderService pillReminderService;
    @Mock
    PillRegimenResponse pillRegimenResponse;
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;
    private OutboxContextForTest context;

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DosageUtil.class);
        playAdherenceTrendFeedbackCommand = new PlayAdherenceTrendFeedbackCommand(null, null);
        PrivateAccessor.setField(playAdherenceTrendFeedbackCommand, "pillReminderService", pillReminderService);
        PrivateAccessor.setField(playAdherenceTrendFeedbackCommand, "allDosageAdherenceLogs", allDosageAdherenceLogs);
        context = new OutboxContextForTest();
    }

    @Test
    public void testExecute() {
        String externalId = "someExternalId";
        String pillRegimenId = "pillRegimenId";
        int successCountThisWeek = 23;
        int successCountLastWeek = 23;
        int scheduledDosageCount = 28;
        String[] result;

        context.partyId(externalId);
        Mockito.when(pillReminderService.getPillRegimen(Mockito.anyString())).thenReturn(pillRegimenResponse);
        Mockito.when(pillRegimenResponse.getPillRegimenId()).thenReturn(pillRegimenId);
        DateTime now = DateUtil.now();
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(pillRegimenId, now.minusWeeks(4).toLocalDate(), now.toLocalDate())).thenReturn(successCountThisWeek);
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(pillRegimenId, now.minusWeeks(5).toLocalDate(), now.minusWeeks(1).toLocalDate())).thenReturn(successCountLastWeek);
        PowerMockito.when(DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(PillRegimenResponse.class))).thenReturn(scheduledDosageCount);


        result = playAdherenceTrendFeedbackCommand.execute(context);

        Assert.assertEquals(result[0], TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);

    }
}
