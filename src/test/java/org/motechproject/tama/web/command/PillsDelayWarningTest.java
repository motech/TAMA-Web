package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRMessageBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillsDelayWarningTest {

    IVRContext context;
    IVRRequest request;
    PillsDelayWarning pillsDelayWarning;

    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private IVRMessageBuilder ivrMessageBuilder;

    @Before
    public void setup() {
        initMocks(this);
        pillsDelayWarning = new PillsDelayWarning(pillReminderService, ivrMessageBuilder);
        context = mock(IVRContext.class);
        request = mock(IVRRequest.class);
        when(context.ivrRequest()).thenReturn(request);
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
        params.put(PillReminderCall.REGIMEN_ID, "regimen_id");
        params.put(PillReminderCall.DOSAGE_ID, "dosage_id");
        params.put(PillReminderCall.TIMES_SENT, "0");
        params.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "1");
        when(request.getTamaParams()).thenReturn(params);

        DateTime time = new DateTime(2011, 1, 1, 10, 20, 0, 0);
        when(pillReminderService.getNextDosageTime("regimen_id", "dosage_id")).thenReturn(time);

        List<String> timeMessages = new ArrayList<String>();
        timeMessages.add("time");
        when(ivrMessageBuilder.getWavs(time)).thenReturn(timeMessages);

        String[] messages = pillsDelayWarning.execute(context);
        assertEquals(IVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("time", messages[1]);
    }
}

