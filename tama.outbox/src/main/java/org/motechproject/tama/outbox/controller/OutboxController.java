package org.motechproject.tama.outbox.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.OUTBOX_URL)
public class OutboxController extends SafeIVRController {
    private VoiceOutboxService outboxService;
    private VoiceMessageResponseFactory messageResponseFactory;

    @Autowired
    public OutboxController(VoiceOutboxService outboxService, IVRMessage ivrMessage, VoiceMessageResponseFactory messageResponseFactory, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.outboxService = outboxService;
        this.messageResponseFactory = messageResponseFactory;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        OutboxContext outboxContext = getOutboxContext(kooKooIVRContext);
        KookooIVRResponseBuilder ivrResponseBuilder = KookooResponseFactory.empty(outboxContext.callId()).language(outboxContext.preferredLanguage());
        OutboundVoiceMessage nextMessage = outboxService.nextMessage(outboxContext.lastPlayedMessageId(), outboxContext.partyId());
        if (nextMessage == null && outboxContext.lastPlayedMessageId() == null) {
            outboxContext.outboxCompleted();
            return ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NO_MESSAGES);
        }
        if (nextMessage == null) {
            outboxContext.outboxCompleted();
            return ivrResponseBuilder.withPlayAudios(TamaIVRMessage.THESE_WERE_YOUR_MESSAGES_FOR_NOW);
        }

        outboxContext.lastPlayedMessageId(nextMessage.getId());
        messageResponseFactory.voiceMessageResponse(kooKooIVRContext, outboxContext, nextMessage, ivrResponseBuilder);
        return ivrResponseBuilder;
    }

    protected OutboxContext getOutboxContext(KooKooIVRContext kooKooIVRContext) {
        return new OutboxContext(kooKooIVRContext);
    }
}