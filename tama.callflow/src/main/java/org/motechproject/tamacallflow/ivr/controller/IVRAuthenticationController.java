package org.motechproject.tamacallflow.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamadomain.domain.IVRAuthenticationStatus;
import org.motechproject.tamacallflow.ivr.StandardIVRResponse;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.platform.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.AUTHENTICATION_URL)
public class IVRAuthenticationController extends SafeIVRController {
    private AuthenticationService authenticationService;
    private TAMAIVRContextFactory contextFactory;

    @Autowired
    public IVRAuthenticationController(AuthenticationService authenticationService, KookooCallDetailRecordsService callDetailRecordsService, IVRMessage ivrMessage, StandardResponseController standardResponseController) {
        this(authenticationService, callDetailRecordsService, ivrMessage, new TAMAIVRContextFactory(), standardResponseController);
    }

    public IVRAuthenticationController(AuthenticationService authenticationService, KookooCallDetailRecordsService callDetailRecordsService, IVRMessage ivrMessage, TAMAIVRContextFactory contextFactory, StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.authenticationService = authenticationService;
        this.contextFactory = contextFactory;
    }

    @Override
    public KookooIVRResponseBuilder newCall(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = contextFactory.initialize(kooKooIVRContext);
        String callerId = tamaivrContext.requestedCallerId();
        String callId = tamaivrContext.callId();

        return authenticationService.allowAccess(callerId, callId) ?
                StandardIVRResponse.signatureTuneAndCollectDTMF(callId) : StandardIVRResponse.endOfCallTuneAndHangup(callId);
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = contextFactory.create(kooKooIVRContext);
        String callId = tamaivrContext.callId();

        IVRAuthenticationStatus authenticationStatus = authenticationService.checkAccess(tamaivrContext);
        if (!authenticationStatus.isFound() ||
                (authenticationStatus.isAuthenticated() && (!authenticationStatus.allowCall()))) {
            return StandardIVRResponse.hangup();
        }
        if (!authenticationStatus.isAuthenticated() && authenticationStatus.doAllowRetry()) {
            tamaivrContext.numberOfLoginAttempts(authenticationStatus.loginAttemptNumber());
            return StandardIVRResponse.signatureTuneAndCollectDTMF(callId);
        }

        tamaivrContext.userAuthenticated(authenticationStatus);
        return KookooResponseFactory.empty(callId);
    }
}
