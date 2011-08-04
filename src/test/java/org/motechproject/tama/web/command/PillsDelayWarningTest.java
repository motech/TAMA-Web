package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.builder.IVRMessageBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillsDelayWarningTest {

    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest request;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRMessageBuilder ivrMessageBuilder;

    private PillsDelayWarning pillsDelayWarning;

    @Before
    public void setup() {
        initMocks(this);
        pillsDelayWarning = new PillsDelayWarning(ivrMessageBuilder);
        when(context.ivrRequest()).thenReturn(request);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
    }

    @Test
    public void shouldReturnPleaseTakeDoseMessageForNonLastReminder() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.TIMES_SENT, "0");
        params.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "2");
        when(request.getTamaParams()).thenReturn(params);
        assertArrayEquals(new String[]{
                IVRMessage.PLEASE_TAKE_DOSE,
                TAMAConstants.RETRY_INTERVAL,
                IVRMessage.MINUTES},
                pillsDelayWarning.execute(context));
    }

    @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        params.put(PillReminderCall.TIMES_SENT, "0");
        params.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "1");
        when(request.getTamaParams()).thenReturn(params);

        List<String> timeMessages = new ArrayList<String>();
        timeMessages.add("time");
        when(ivrMessageBuilder.getWavs(any(DateTime.class))).thenReturn(timeMessages);

        String[] messages = pillsDelayWarning.execute(context);
        assertEquals(IVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("time", messages[1]);
    }
}

