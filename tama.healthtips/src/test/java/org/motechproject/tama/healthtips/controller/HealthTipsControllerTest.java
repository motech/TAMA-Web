package org.motechproject.tama.healthtips.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.healthtips.constants.HealthTipPropertiesForTest;
import org.motechproject.tama.healthtips.criteria.ContinueToHealthTipsCriteria;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    @Mock
    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    private HealthTipsController healthTipsController;
    private String patientId;

    @Before
    public void setUp() {
        initMocks(this);
        patientId = "patientId";
        HealthTipPropertiesForTest properties = new HealthTipPropertiesForTest();
        healthTipsController = new HealthTipsController(
                healthTipService,
                tamaIvrMessage,
                callDetailRecordsService,
                standardResponseController,
                properties,
                tamaivrContextFactory,
                continueToHealthTipsCriteria
        );
        when(kookooIVRContext.callId()).thenReturn("34");
        when(kookooIVRContext.preferredLanguage()).thenReturn("en");
        when(kookooIVRContext.externalId()).thenReturn(patientId);
        when(tamaivrContextFactory.create(kookooIVRContext)).thenReturn(tamaIVRContext);
        when(tamaIVRContext.getPlayedHealthTipsCount()).thenReturn(0);
    }

    @Test
    public void shouldPlayKookooPlayAudioFromPlaylist() {
        when(healthTipService.nextHealthTip(patientId)).thenReturn("yourHealthTipsIs");
        when(continueToHealthTipsCriteria.shouldContinue(patientId)).thenReturn(true);

        assertEquals("yourHealthTipsIs", healthTipsController.gotDTMF(kookooIVRContext).getPlayAudios().get(0));
        verify(tamaIVRContext).setLastPlayedHealthTip("yourHealthTipsIs");
        verify(tamaIVRContext).setPlayedHealthTipsCount(1);
    }

    @Test
    public void shouldPlayNoHealthTipsMessage_IfAllHealthTipsAreExhausted() {
        when(healthTipService.nextHealthTip(patientId)).thenReturn("");
        when(continueToHealthTipsCriteria.shouldContinue(patientId)).thenReturn(true);

        assertEquals("010_11_04_NoHealthTips", healthTipsController.gotDTMF(kookooIVRContext).getPlayAudios().get(0));
        verify(tamaIVRContext).callState(CallState.END_OF_HEALTH_TIPS_FLOW);
        verify(tamaIVRContext).setPlayedHealthTipsCount(0);
    }

    @Test
    public void shouldNotContinueToHealthTipsWhenContinueToHealthTipsCriteriaNotSatisfied() {
        when(healthTipService.nextHealthTip(patientId)).thenReturn("");
        when(continueToHealthTipsCriteria.shouldContinue(patientId)).thenReturn(false);

        assertTrue(healthTipsController.gotDTMF(kookooIVRContext).isEmpty());
        verify(tamaIVRContext).callState(CallState.END_OF_HEALTH_TIPS_FLOW);
        verify(tamaIVRContext).setPlayedHealthTipsCount(0);
    }
}
