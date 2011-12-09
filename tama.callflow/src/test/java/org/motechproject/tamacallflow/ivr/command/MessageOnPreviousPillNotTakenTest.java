package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageOnPreviousPillNotTakenTest {
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillNotTaken = new MessageOnPreviousPillNotTaken(new IVRDayMessageBuilder(), null);
    }

    @Test
    public void shouldReturnPillNotTakenMessage() {
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        TAMAIVRContextForTest context = new TAMAIVRContextForTest().dosageId("currentDosageId").callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).pillRegimen(pillRegimenResponse);
        context.callDirection(CallDirection.Outbound);
        String[] messages = messageOnPreviousPillNotTaken.executeCommand(context);
        assertEquals(5, messages.length);
        assertEquals(TamaIVRMessage.YOU_SAID_YOU_DID_NOT_TAKE, messages[0]);
        assertEquals(TamaIVRMessage.MORNING_CONFIRMATION, messages[1]);
        assertEquals(TamaIVRMessage.DOSE_NOT_TAKEN, messages[2]);
        assertEquals(TamaIVRMessage.DOSE_RECORDED, messages[3]);
        assertEquals(TamaIVRMessage.TRY_NOT_TO_MISS, messages[4]);
    }
}
