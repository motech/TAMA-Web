package org.motechproject.tamacallflow.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.MENU_REPEAT)
public class MenuRepeatController extends SafeIVRController {
    private TAMAIVRContextFactory ivrContextFactory;

    public MenuRepeatController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory ivrContextFactory, StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.ivrContextFactory = ivrContextFactory;
    }

    @Autowired
    public MenuRepeatController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory(), standardResponseController);
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext ivrContext = ivrContextFactory.create(kooKooIVRContext);
        ivrContext.resetForMenuRepeat();
        return new KookooIVRResponseBuilder().withSid(ivrContext.callId()).withPlayAudios(TamaIVRMessage.HANGUP_OR_MAIN_MENU).
                language(ivrContext.preferredLanguage());

    }
}
