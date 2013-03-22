package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MenuRepeatControllerTest {
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private TAMAIVRContext tamaivrContext;

    private MenuRepeatController menuRepeatController;

    @Before
    public void setUp() {
        initMocks(this);
        when(tamaivrContextFactory.create(any(KooKooIVRContext.class))).thenReturn(tamaivrContext);
        menuRepeatController = new MenuRepeatController(ivrMessage, kookooCallDetailRecordsService, tamaivrContextFactory, standardResponseController);
    }

    @Test
    public void shouldResetContextForRepeatingTheCallFLow() {
        menuRepeatController.gotDTMF(null);
        verify(tamaivrContext).resetForMenuRepeat();
    }

    @Test
    public void shouldAddPromptForHangUp() {
        when(tamaivrContext.doNotPromptForHangUp()).thenReturn(false);
        KookooIVRResponseBuilder responseBuilder = menuRepeatController.gotDTMF(null);
        assertTrue(responseBuilder.getPlayAudios().contains(TamaIVRMessage.HANGUP_OR_MAIN_MENU));
    }

    @Test
    public void shouldNotAddPromptForHangUp() {
        when(tamaivrContext.doNotPromptForHangUp()).thenReturn(true);
        KookooIVRResponseBuilder responseBuilder = menuRepeatController.gotDTMF(null);
        assertFalse(responseBuilder.getPlayAudios().contains(TamaIVRMessage.HANGUP_OR_MAIN_MENU));
    }
}