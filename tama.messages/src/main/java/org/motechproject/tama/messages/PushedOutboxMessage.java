package org.motechproject.tama.messages;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushedOutboxMessage {

    private VoiceOutboxService voiceOutboxService;
    private VoiceMessageResponseFactory voiceMessageResponseFactory;

    @Autowired
    public PushedOutboxMessage(VoiceOutboxService voiceOutboxService, VoiceMessageResponseFactory voiceMessageResponseFactory) {
        this.voiceOutboxService = voiceOutboxService;
        this.voiceMessageResponseFactory = voiceMessageResponseFactory;
    }

    public boolean addToResponse(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        return playNextMessage(ivrResponseBuilder, kooKooIVRContext, tamaivrContext);
    }

    public void markLastPlayedMessageAsRead(KooKooIVRContext kooKooIVRContext, TAMAIVRContext tamaivrContext) {
        OutboxContext outboxContext = new OutboxContext(kooKooIVRContext);
        /*Next message is idempotent. Hence it will return the same message until the message is marked as read*/
        voiceOutboxService.nextMessage(outboxContext.lastPlayedMessageId(), tamaivrContext.patientDocumentId());
    }

    private boolean playNextMessage(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kooKooIVRContext, TAMAIVRContext tamaivrContext) {
        if (hasMessageOfType(tamaivrContext, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)) {
            return addToResponse(ivrResponseBuilder, tamaivrContext.patientDocumentId(), kooKooIVRContext, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
        } else if (hasMessageOfType(tamaivrContext, TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE)) {
            return addToResponse(ivrResponseBuilder, tamaivrContext.patientDocumentId(), kooKooIVRContext, TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE);
        } else if (hasMessageOfType(tamaivrContext, TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO)) {
            return addToResponse(ivrResponseBuilder, tamaivrContext.patientDocumentId(), kooKooIVRContext, TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        } else {
            return false;
        }
    }

    private boolean addToResponse(KookooIVRResponseBuilder ivrResponseBuilder, String patientDocumentId, KooKooIVRContext kooKooIVRContext, String messageType) {
        OutboxContext outboxContext = new OutboxContext(kooKooIVRContext);
        OutboundVoiceMessage outboundVoiceMessage = voiceOutboxService.nextMessage(null, patientDocumentId, messageType);
        if (null != outboundVoiceMessage) {
            voiceMessageResponseFactory.voiceMessageResponse(kooKooIVRContext, new OutboxContext(kooKooIVRContext), outboundVoiceMessage, ivrResponseBuilder);
            outboxContext.lastPlayedMessageId(outboundVoiceMessage.getId());
            return true;
        } else {
            return false;
        }
    }

    private boolean hasMessageOfType(TAMAIVRContext tamaivrContext, String messageType) {
        return countMessagesOfType(tamaivrContext, messageType) != 0;
    }

    private int countMessagesOfType(TAMAIVRContext tamaivrContext, String messageType) {
        return voiceOutboxService.getNumberOfMessages(tamaivrContext.patientDocumentId(), OutboundVoiceMessageStatus.PENDING, messageType);
    }
}
