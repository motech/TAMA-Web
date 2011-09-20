package org.motechproject.tama.web.command;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
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
public class MessageOnPreviousPillTakenTest {
    @Mock
    private IVRContext context;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRRequest request;
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPreviousPillTaken = new MessageOnPreviousPillTaken(new IVRDayMessageBuilder(new TamaIVRMessage(null, new FileUtil())));
        when(context.ivrRequest()).thenReturn(request);
    }

    @Test
    public void shouldReturnPillTakenMessage() {
    	when(request.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());

        //TODO: Previous dosage  case
        /*mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));

        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));*/
        String[] messages = messageOnPreviousPillTaken.execute(context);
        assertEquals(3, messages.length);
        assertEquals(TamaIVRMessage.YOU_SAID_YOU_TOOK, messages[0]);
        assertEquals(TamaIVRMessage.MORNING, messages[1]);
        assertEquals(TamaIVRMessage.DOSE, messages[2]);
    }
}
