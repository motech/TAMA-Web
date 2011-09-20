package org.motechproject.tama.web.command;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
    private String retryInterval;

    @Before
    public void setup() {
        initMocks(this);
        Properties stubProperties = new Properties();
        retryInterval = "15";
        stubProperties.put(TAMAConstants.RETRY_INTERVAL, retryInterval);
        pillsDelayWarning = new PillsDelayWarning(new IVRDayMessageBuilder(new TamaIVRMessage(null, new FileUtil())), new TamaIVRMessage(null, new FileUtil()),stubProperties);
        when(context.ivrRequest()).thenReturn(request);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
    }

    @Test
    public void shouldReturnPleaseTakeDoseMessageForNonLastReminder() {
        when(request.getParameter(PillReminderCall.TIMES_SENT)).thenReturn("0");
        when(request.getParameter(PillReminderCall.TOTAL_TIMES_TO_SEND)).thenReturn("2");
        assertArrayEquals(new String[]{
                TamaIVRMessage.PLEASE_TAKE_DOSE,
                String.format("Num_%03d",Integer.valueOf(retryInterval)),
                TamaIVRMessage.CALL_AFTER_SOME_TIME},
                pillsDelayWarning.execute(context));
    }

    @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {
        when(request.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
        when(request.getParameter(PillReminderCall.TIMES_SENT)).thenReturn("1");
        when(request.getParameter(PillReminderCall.TOTAL_TIMES_TO_SEND)).thenReturn("1");

        String[] messages = pillsDelayWarning.execute(context);
        assertEquals(6, messages.length);
        assertEquals(TamaIVRMessage.LAST_REMINDER_WARNING, messages[0]);
        assertEquals("Num_010", messages[1]);
        assertEquals("Num_005", messages[2]);
        assertEquals("001_07_04_doseTimeAtEvening", messages[3]);
        assertEquals("timeOfDayToday", messages[4]);
        assertEquals("005_04_03_WillCallAgain", messages[5]);
    }
}

