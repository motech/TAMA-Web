package org.motechproject.tama.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ClinicNameMessageBuilderTest {

    @Mock
    private CMSLiteService cmsLiteService;

    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    private Clinic clinic;

    @Before
    public void setUp(){
        initMocks(this);
        clinicNameMessageBuilder = new ClinicNameMessageBuilder(cmsLiteService);
    }

    @Test
    public void shouldReturnDefaultOutboundMessage_WhenClinicSpecificMessageNotAvailable(){
        clinic = ClinicBuilder.startRecording().withId("testClinicId").withName("testClinicName").build();
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");

        when(cmsLiteService.isStreamContentAvailable("en", "testclinicname.wav")).thenReturn(false);
        assertEquals("001_02_01_TAMAGreeting1Generic", clinicNameMessageBuilder.getOutboundMessage(clinic, ivrLanguage));
    }

    @Test
    public void shouldReturnOutboundClinicMessage_WhenClinicSpecificMessageAvailable(){
        clinic = ClinicBuilder.startRecording().withId("testClinicId").withName("testClinicName").build();
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");

        when(cmsLiteService.isStreamContentAvailable("en", "testclinicname.wav")).thenReturn(true);
        assertEquals(clinic.getName(), clinicNameMessageBuilder.getOutboundMessage(clinic, ivrLanguage));
    }

    @Test
    public void shouldReturnDefaultInboundMessage_WhenClinicSpecificMessageNotAvailable(){
        clinic = ClinicBuilder.startRecording().withId("testClinicId").withName("testClinicName").build();
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");

        when(cmsLiteService.isStreamContentAvailable("en", "testclinicname.wav")).thenReturn(false);
        assertEquals("Greeting2Generic", clinicNameMessageBuilder.getInboundMessage(clinic, ivrLanguage));
    }

    @Test
    public void shouldReturnInboundClinicMessage_WhenClinicSpecificMessageAvailable(){
        clinic = ClinicBuilder.startRecording().withId("testClinicId").withName("testClinicName").build();
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");

        when(cmsLiteService.isStreamContentAvailable("en", "testclinicname.wav")).thenReturn(true);
        assertEquals("welcome_to_testClinicName", clinicNameMessageBuilder.getInboundMessage(clinic, ivrLanguage));
    }
}
