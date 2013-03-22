package org.motechproject.tama.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.MENU_REPEAT)
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
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(ivrContext.callId());
        if (!ivrContext.doNotPromptForHangUp()) {
            response.withPlayAudios(TamaIVRMessage.HANGUP_OR_MAIN_MENU);
        } else {
            ivrContext.doNoPromptForHangUp(false);
        }
        ivrContext.resetForMenuRepeat();
        return response.language(ivrContext.preferredLanguage());
    }
}
