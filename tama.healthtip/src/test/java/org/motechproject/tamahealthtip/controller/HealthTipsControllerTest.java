package org.motechproject.tamahealthtip.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.ivr.factory.VoiceMessageResponseFactory;
import org.motechproject.tamahealthtip.constants.HealthTipPropertiesForTest;
import org.motechproject.tamahealthtip.domain.HealthTipsProperties;
import org.motechproject.tamahealthtip.service.HealthTipService;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
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
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private TAMAIVRContext tamaIVRContext;

    private HealthTipsController healthTipsController;

    @Before
    public void setUp() {
        initMocks(this);
        String patientId = "patientId";
        HealthTipPropertiesForTest properties = new HealthTipPropertiesForTest();
        healthTipsController = new HealthTipsController(healthTipService, tamaIvrMessage,
                                   callDetailRecordsService, standardResponseController, properties, tamaivrContextFactory);
        when(kookooIVRContext.callId()).thenReturn("34");
        when(kookooIVRContext.preferredLanguage()).thenReturn("en");
        when(kookooIVRContext.externalId()).thenReturn(patientId);
        when(healthTipService.nextHealthTip(patientId)).thenReturn("fuuQux.wav");
        when(tamaivrContextFactory.create(kookooIVRContext)).thenReturn(tamaIVRContext);
        when(tamaIVRContext.getPlayedHealthTipsCount()).thenReturn(0);
    }

    @Test
    public void shouldPlayKookooPlayAudioFromPlaylist() {
        assertEquals("fuuQux.wav", healthTipsController.gotDTMF(kookooIVRContext).getPlayAudios().get(0));
        verify(tamaIVRContext).setLastPlayedHealthTip("fuuQux.wav");
        verify(tamaIVRContext).setPlayedHealthTipsCount(1);
    }
}
