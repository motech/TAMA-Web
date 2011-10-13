package org.motechproject.tama.ivr.ivrauthentication;

import org.motechproject.ivr.kookoo.HangupException;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.TAMACallFlowController;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TamaIVRMessage;
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
    public KookooIVRResponseBuilder newCall(KooKooIVRContext kooKooIVRContext) throws HangupException {
        TAMAIVRContext tamaivrContext = contextFactory.initialize(kooKooIVRContext);
        String callerId = tamaivrContext.requestedCallerId();
        String callId = tamaivrContext.callId();

        if (!authenticationService.allowAccess(callerId, callId)) {
            throw new HangupException("Access not allowed");
        }
        return signatureTune(callId);
    }

    private KookooIVRResponseBuilder signatureTune(String callId) {
        return new KookooIVRResponseBuilder().withSid(callId).withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC).collectDtmfLength(4);
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) throws HangupException {
        TAMAIVRContext tamaivrContext = contextFactory.create(kooKooIVRContext);
        String passcode = tamaivrContext.dtmfInput();
        String phoneNumber = tamaivrContext.callerId();
        int attemptNumber = tamaivrContext.numberOfLoginAttempts();
        String callId = tamaivrContext.callId();

        IVRAuthenticationStatus authenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, attemptNumber + 1, callId);
        if (!authenticationStatus.isFound() ||
                (authenticationStatus.isAuthenticated() && (!authenticationStatus.isActive()))) {
            throw new HangupException("Patient not authenticated");
        }
        if (!authenticationStatus.isAuthenticated() && authenticationStatus.doAllowRetry()) {
            tamaivrContext.numberOfLoginAttempts(authenticationStatus.loginAttemptNumber());
            return signatureTune(callId);
        }

        tamaivrContext.userAuthenticated(authenticationStatus);
        return KookooResponseFactory.empty(callId);
    }
}
