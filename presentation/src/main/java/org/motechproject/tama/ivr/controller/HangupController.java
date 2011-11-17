package org.motechproject.tama.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.HANG_UP_URL)
public class HangupController extends SafeIVRController {
    private TAMAIVRContextFactory ivrContextFactory;

    public HangupController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory ivrContextFactory) {
        super(ivrMessage, callDetailRecordsService);
        this.ivrContextFactory = ivrContextFactory;
    }

    @Autowired
    public HangupController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory());
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = ivrContextFactory.create(kooKooIVRContext);
        return new KookooIVRResponseBuilder().withSid(ivrContext.callId()).withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC).
                language(ivrContext.preferredLanguage()).withHangUp();
    }
}
