package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.TAMAConstants;
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PillsDelayWarningTest {

    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest request;
    @Mock
    private IVRSession ivrSession;

    private PillsDelayWarning pillsDelayWarning;

    @Before
    public void setup() {
        initMocks(this);
        pillsDelayWarning = new PillsDelayWarning();
        when(context.ivrRequest()).thenReturn(request);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
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
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        params.put(PillReminderCall.TIMES_SENT, "1");
        params.put(PillReminderCall.TOTAL_TIMES_TO_SEND, "1");
        when(request.getTamaParams()).thenReturn(params);


        String[] messages = pillsDelayWarning.execute(context);
        assertEquals(5, messages.length);
        assertEquals(IVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("10", messages[1]);
        assertEquals("5", messages[2]);
        assertEquals("in_the_evening", messages[3]);
        assertEquals("today", messages[4]);
    }
}

