package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageOnPreviousPillNotTakenTest {
    @Mock
    private IVRContext context;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRRequest request;
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillNotTaken = new MessageOnPreviousPillNotTaken();
        when(context.ivrRequest()).thenReturn(request);
    }

    @Test
    public void shouldReturnPillNotTakenMessage() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");

        when(request.getTamaParams()).thenReturn(params);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());

        //TODO: Previous dosage case
       /* mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));*/

        String[] messages = messageOnPreviousPillNotTaken.execute(context);
        assertEquals(4, messages.length);
        assertEquals(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE, messages[0]);
        assertEquals(IVRMessage.MORNING, messages[1]);
        assertEquals(IVRMessage.DOSE, messages[2]);
        assertEquals(IVRMessage.TRY_NOT_TO_MISS, messages[3]);
    }
}
