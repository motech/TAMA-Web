package org.motechproject.tama.healthtips.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.healthtips.constants.HealthTipPropertiesForTest;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

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