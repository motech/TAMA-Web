package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageOnPreviousPillTakenTest {
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillTaken = new MessageOnPreviousPillTaken(null);
    }

    @Test
    public void shouldReturnPillTakenMessage() {
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest().callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).callDirection(CallDirection.Outbound).callStartTime(new DateTime(2010, 10, 10, 16, 0, 0));
        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(tamaivrContextForTest).pillRegimen(pillRegimenResponse);
        String[] messages = messageOnPreviousPillTaken.executeCommand(context);
        assertEquals(4, messages.length);
        assertEquals(TamaIVRMessage.YOU_SAID_YOU_TOOK, messages[0]);
        assertEquals(TamaIVRMessage.MORNING_CONFIRMATION, messages[1]);
        assertEquals(TamaIVRMessage.DOSE_TAKEN, messages[2]);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[3]);
    }
}
