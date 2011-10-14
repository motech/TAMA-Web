package org.motechproject.tama.ivr.ivrauthentication;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.AUTHENTICATION_URL)
public class IVRAuthenticationController extends SafeIVRController {
    private AuthenticationService authenticationService;
    private TAMAIVRContextFactory contextFactory;

    @Autowired
    public IVRAuthenticationController(AuthenticationService authenticationService, KookooCallDetailRecordsService callDetailRecordsService, IVRMessage ivrMessage) {
        this(authenticationService, callDetailRecordsService, ivrMessage, new TAMAIVRContextFactory());
    }

    public IVRAuthenticationController(AuthenticationService authenticationService, KookooCallDetailRecordsService callDetailRecordsService, IVRMessage ivrMessage, TAMAIVRContextFactory contextFactory) {
        super(ivrMessage, callDetailRecordsService);
        this.authenticationService = authenticationService;
        this.contextFactory = contextFactory;
    }

    @Override
    public KookooIVRResponseBuilder newCall(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = contextFactory.initialize(kooKooIVRContext);
        String callerId = tamaivrContext.requestedCallerId();
        String callId = tamaivrContext.callId();

        return authenticationService.allowAccess(callerId, callId) ?
                StandardIVRResponse.signatureTuneAndCollectDTMF(callId) : StandardIVRResponse.signatureTuneAndHangup(callId);
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = contextFactory.create(kooKooIVRContext);
        String passcode = tamaivrContext.dtmfInput();
        String phoneNumber = tamaivrContext.callerId();
        int attemptNumber = tamaivrContext.numberOfLoginAttempts();
        String callId = tamaivrContext.callId();

        IVRAuthenticationStatus authenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, attemptNumber + 1, callId);
        if (!authenticationStatus.isFound() ||
                (authenticationStatus.isAuthenticated() && (!authenticationStatus.isActive()))) {
            return StandardIVRResponse.signatureTuneAndHangup(callId);
        }
        if (!authenticationStatus.isAuthenticated() && authenticationStatus.doAllowRetry()) {
            tamaivrContext.numberOfLoginAttempts(authenticationStatus.loginAttemptNumber());
            return StandardIVRResponse.signatureTuneAndCollectDTMF(callId);
        }

        tamaivrContext.userAuthenticated(authenticationStatus);
        return KookooResponseFactory.empty(callId);
    }
}
