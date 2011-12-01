package org.motechproject.tamacallflow.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.controller.MenuRepeatController;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;

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

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldResetContextForRepeatingTheCallFLow(){
        when(tamaivrContextFactory.create(any(KooKooIVRContext.class))).thenReturn(tamaivrContext);

        MenuRepeatController menuRepeatController = new MenuRepeatController(ivrMessage, kookooCallDetailRecordsService, tamaivrContextFactory, standardResponseController);
        menuRepeatController.gotDTMF(null);

        verify(tamaivrContext).resetForMenuRepeat();
        
    }
}