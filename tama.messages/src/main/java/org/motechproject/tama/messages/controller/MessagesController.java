package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.MessagesToBePushed;
import org.motechproject.tama.messages.domain.PushedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.PUSH_MESSAGES_URL)
public class MessagesController extends SafeIVRController {

    private MessagesToBePushed messagesToBePushed;

    @Autowired
    public MessagesController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, MessagesToBePushed messagesToBePushed) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.messagesToBePushed = messagesToBePushed;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        PushedMessage pushedMessage = new PushedMessage(kooKooIVRContext);
        if (pushedMessage.exists()) {
            messagesToBePushed.markAsRead(kooKooIVRContext, pushedMessage);
            new TAMAIVRContextFactory().create(kooKooIVRContext).callState(CallState.PUSH_MESSAGES_COMPLETE);
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        } else {
            return messagesToBePushed.nextMessage(kooKooIVRContext);
        }
    }
}
