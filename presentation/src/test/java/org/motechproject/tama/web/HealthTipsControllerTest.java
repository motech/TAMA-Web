package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.service.HealthTipService;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HealthTipsControllerTest {

    @Mock
    HealthTipService healthTipService;
    @Mock
    private TamaIVRMessage tamaIvrMessage;
    @Mock
    private VoiceMessageResponseFactory messageResponseFactory;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    HealthTipsController healthTipsController;
    @Mock
    private Cookies cookie;

    @Before
    public void setUp(){
        initMocks(this);
        String patientId = "patientId";
        healthTipsController = new HealthTipsController(healthTipService, tamaIvrMessage, callDetailRecordsService, standardResponseController);
        when(kookooIVRContext.callId()).thenReturn("34");
        when(kookooIVRContext.preferredLanguage()).thenReturn("en");
        when(kookooIVRContext.externalId()).thenReturn(patientId);
        when(healthTipService.getPlayList(patientId)).thenReturn(Arrays.asList("fooBar.wav", "fuuQux.wav"));
        when(kookooIVRContext.cookies()).thenReturn(cookie);
    }

    @Test
    public void shouldPlayKookooPlayAudioFromPlaylist() {
        assertEquals("fooBar.wav", healthTipsController.gotDTMF(kookooIVRContext).getPlayAudios().get(0));
        verify(cookie).add(HealthTipsController.LAST_PLAYED_HEALTH_TIP, "fooBar.wav");
        verify(cookie).add(HealthTipsController.HEALTH_TIPS_PLAYED_COUNT, "1");
    }
}
