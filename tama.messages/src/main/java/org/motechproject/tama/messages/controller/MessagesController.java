package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.PUSH_MESSAGES_URL)
public class MessagesController extends SafeIVRController {

    private PullMessagesController pullMessagesController;
    private PushMessagesController pushMessagesController;

    @Autowired
    public MessagesController(IVRMessage ivrMessage,
                              KookooCallDetailRecordsService callDetailRecordsService,
                              StandardResponseController standardResponseController,
                              PullMessagesController pullMessagesController,
                              PushMessagesController pushMessagesController) {

        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.pullMessagesController = pullMessagesController;
        this.pushMessagesController = pushMessagesController;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (CallState.PULL_MESSAGES.equals(tamaivrContext.callState())) {
            return sanitizeResponse(kooKooIVRContext, handleDTMF(kooKooIVRContext, pullMessagesController), CallState.PULL_MESSAGES_TREE);
        } else {
            return sanitizeResponse(kooKooIVRContext, handleDTMF(kooKooIVRContext, pushMessagesController), CallState.PUSH_MESSAGES_COMPLETE);
        }
    }

    private KookooIVRResponseBuilder handleDTMF(KooKooIVRContext context, PatientMessagesController messagesController) {
        if (messagesController.markAsReadAndContinue(context)) {
            return messagesController.gotDTMF(context);
        } else {
            return new KookooIVRResponseBuilder().language(context.preferredLanguage()).withSid(context.callId());
        }
    }

    private KookooIVRResponseBuilder sanitizeResponse(KooKooIVRContext context, KookooIVRResponseBuilder builder, CallState state) {
        if (builder.isEmpty()) {
            endMessagesFlow(context, new PlayedMessage(context), state);
        }
        return builder;
    }

    private void endMessagesFlow(KooKooIVRContext kooKooIVRContext, PlayedMessage playedMessage, CallState state) {
        playedMessage.reset();
        new TAMAIVRContextFactory().create(kooKooIVRContext).callState(state);
    }
}
