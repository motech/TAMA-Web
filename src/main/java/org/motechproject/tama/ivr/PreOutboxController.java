package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.PRE_OUTBOX_URL)
public class PreOutboxController extends SafeIVRController {
    private TAMAIVRContextFactory ivrContextFactory;

    public PreOutboxController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory ivrContextFactory) {
        super(ivrMessage, callDetailRecordsService);
        this.ivrContextFactory = ivrContextFactory;
    }

    @Autowired
    public PreOutboxController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory());
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = ivrContextFactory.create(kooKooIVRContext);
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.CONTINUE_TO_OUTBOX);
        ivrContext.callState(CallState.OUTBOX);
        return ivrResponseBuilder;
    }
}
