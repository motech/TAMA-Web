package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.platform.service.AuthenticationService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRAuthenticationControllerTest {
    @Mock
    AuthenticationService authenticationService;
    @Mock
    KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    IVRMessage ivrMessage;
    @Mock
    TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private StandardResponseController standardResponseController;

    private String callId = "123";
    private String callerId = "9999";
    private IVRAuthenticationController ivrAuthenticationController;
    private TAMAIVRContextForTest tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        ivrAuthenticationController = new IVRAuthenticationController(authenticationService, callDetailRecordsService, ivrMessage, tamaivrContextFactory, standardResponseController);
        tamaivrContext = new TAMAIVRContextForTest().callId(callId);
    }

    @Test
    public void hangupNewCallIfUserIsNotAllowedAccess() {
        tamaivrContext.requestedCallerId(callerId);
        when(tamaivrContextFactory.initialize(kooKooIVRContext)).thenReturn(tamaivrContext);
        when(authenticationService.allowAccess(callerId, callId)).thenReturn(false);
        KookooIVRResponseBuilder ivrResponseBuilder = ivrAuthenticationController.newCall(kooKooIVRContext);
        assertEquals(true, ivrResponseBuilder.isHangUp());
    }

    @Test
    public void acceptNewCallWhenUserIsAllowedAccess() {
        tamaivrContext.requestedCallerId(callerId);
        when(tamaivrContextFactory.initialize(kooKooIVRContext)).thenReturn(tamaivrContext);
        when(authenticationService.allowAccess(callerId, callId)).thenReturn(true);
        KookooIVRResponseBuilder kookooIVRResponseBuilder = ivrAuthenticationController.newCall(kooKooIVRContext);
        assertEquals(true, kookooIVRResponseBuilder.isCollectDTMF());
    }

    @Test
    public void retryWhenPasswordIsWrong() {
        String dtmfInput = "1234";
        int numberOfLoginAttempts = 0;
        tamaivrContext.dtmfInput(dtmfInput).callerId(callerId);
        tamaivrContext.numberOfLoginAttempts(numberOfLoginAttempts);
        tamaivrContext.callId(callId);
        tamaivrContext.callDirection(CallDirection.Outbound);
        when(authenticationService.checkAccess(tamaivrContext)).thenReturn(IVRAuthenticationStatus.allowRetry(1));
        when(tamaivrContextFactory.create(kooKooIVRContext)).thenReturn(tamaivrContext);
        KookooIVRResponseBuilder kookooIVRResponseBuilder = ivrAuthenticationController.gotDTMF(kooKooIVRContext);
        assertEquals(true, kookooIVRResponseBuilder.isCollectDTMF());
        assertEquals(1, tamaivrContext.numberOfLoginAttempts());
    }
}
