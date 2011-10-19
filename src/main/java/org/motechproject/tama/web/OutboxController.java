package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.ivr.TAMACallFlowController;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.VoiceMessageResponseFactory;
import org.motechproject.tama.outbox.OutboxContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.OUTBOX_URL)
public class OutboxController extends SafeIVRController {
    public static final String VOICE_MESSAGE_COMMAND_AUDIO = "AudioCommand";
    public static final String VOICE_MESSAGE_COMMAND = "command";

    private VoiceOutboxService outboxService;
    private TAMAIVRContextFactory contextFactory;
    private VoiceMessageResponseFactory messageResponseFactory;


    @Autowired
    public OutboxController(VoiceOutboxService outboxService, IVRMessage ivrMessage, VoiceMessageResponseFactory messageResponseFactory, KookooCallDetailRecordsService callDetailRecordsService) {
        this(outboxService, ivrMessage, new TAMAIVRContextFactory(), messageResponseFactory, callDetailRecordsService);
    }

    public OutboxController(VoiceOutboxService outboxService, IVRMessage ivrMessage, TAMAIVRContextFactory contextFactory, VoiceMessageResponseFactory messageResponseFactory, KookooCallDetailRecordsService callDetailRecordsService) {
        super(ivrMessage, callDetailRecordsService);
        this.outboxService = outboxService;
        this.contextFactory = contextFactory;
        this.messageResponseFactory = messageResponseFactory;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        OutboxContext outboxContext = contextFactory.createOutboxContext(kooKooIVRContext);
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
        messageResponseFactory.voiceMessageResponse(outboxContext, nextMessage, ivrResponseBuilder);
        return ivrResponseBuilder;
    }

}