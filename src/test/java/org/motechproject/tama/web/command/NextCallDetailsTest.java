package org.motechproject.tama.web.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class NextCallDetailsTest {

    private IVRContext context;

    @Mock
    private IVRRequest request;

    @Mock
    private IVRSession ivrSession;

    private NextCallDetails nextCallDetails;

    @Before
    public void setup() {
        initMocks(this);
        

        nextCallDetails = new NextCallDetails(new IVRDayMessageBuilder(new TamaIVRMessage(null)));

        context = new IVRContext(request, ivrSession);
        
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));


    }

     @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {

        when(request.getCallDirection()).thenReturn(CallDirection.Inbound);

        String[] messages = nextCallDetails.execute(context);
        assertEquals(7, messages.length);
        assertEquals("010_04_01_nextDoseIs1", messages[0]);
        assertEquals("timeOfDayAt", messages[1]);
        assertEquals("Num_010", messages[2]);
        assertEquals("Num_005", messages[3]);
        assertEquals("001_07_04_doseTimeAtEvening", messages[4]);
        assertEquals("timeOfDayToday", messages[5]);
        assertEquals("010_04_06_nextDoseIs2", messages[6]);
    }
}
