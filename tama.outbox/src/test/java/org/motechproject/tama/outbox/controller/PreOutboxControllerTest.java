package org.motechproject.tama.outbox.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class PreOutboxControllerTest {
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private StandardResponseController standardResponseController;

    @Test
    public void playPreOutboxMessage() {
        initMocks(this);
        PreOutboxController preOutboxController = new PreOutboxController(ivrMessage, callDetailRecordsService, tamaivrContextFactory, standardResponseController);
        TAMAIVRContextForTest ivrContext = new TAMAIVRContextForTest();
        when(tamaivrContextFactory.create(null)).thenReturn(ivrContext);
        KookooIVRResponseBuilder ivrResponseBuilder = preOutboxController.gotDTMF(null);
        assertEquals(true, ivrResponseBuilder.getPlayAudios().contains(TamaIVRMessage.CONTINUE_TO_OUTBOX));
    }
}
