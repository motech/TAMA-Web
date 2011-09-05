package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
        nextCallDetails = new NextCallDetails(new IVRDayMessageBuilder(new IVRMessage(null, new FileUtil())));

        context = new IVRContext(request, ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 16, 0, 0));
    }

     @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {

        when(request.hasNoTamaData()).thenReturn(true);

        String[] messages = nextCallDetails.execute(context);
        assertEquals(6, messages.length);
        assertEquals("010_04_01_nextDoseIs1", messages[0]);
        assertEquals("timeOfDay_At", messages[1]);
        assertEquals("Num_010", messages[2]);
        assertEquals("Num_005", messages[3]);
        assertEquals("001_007_04_evening", messages[4]);
        assertEquals("timeOfDayToday", messages[5]);
    }
}
