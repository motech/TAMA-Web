package org.motechproject.tama.messages;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus.PENDING;

@Component
public class PushedOutboxMessage {

    private VoiceOutboxService voiceOutboxService;
    private VoiceMessageResponseFactory voiceMessageResponseFactory;
    private List<String> voiceMessageTypes;

    @Autowired
    public PushedOutboxMessage(VoiceOutboxService voiceOutboxService, VoiceMessageResponseFactory voiceMessageResponseFactory) {
        this.voiceOutboxService = voiceOutboxService;
        this.voiceMessageResponseFactory = voiceMessageResponseFactory;
        this.voiceMessageTypes = asList(
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE,
                TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE,
                TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO
        );
    }

    public boolean hasAnyMessage(KooKooIVRContext ivrContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(ivrContext);
        boolean result = false;
        for (String voiceMessageType : voiceMessageTypes) {
            result |= hasMessageOfType(tamaivrContext, voiceMessageType);
        }
        return result;
    }

    public KookooIVRResponseBuilder getResponse(KooKooIVRContext kookooIVRContext) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(kookooIVRContext.callId());
        addMessageToResponse(response, kookooIVRContext);
        return response;
    }

    public void markAsRead(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        OutboxContext outboxContext = new OutboxContext(kooKooIVRContext);
        /*Next message is idempotent. Hence it will return the same message until the message is marked as read*/
        voiceOutboxService.nextMessage(outboxContext.lastPlayedMessageId(), tamaivrContext.patientDocumentId());
    }

    private void addMessageToResponse(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kookooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kookooIVRContext);
        for (String voiceMessageType : voiceMessageTypes) {
            if (hasMessageOfType(tamaivrContext, voiceMessageType)) {
                addToResponse(ivrResponseBuilder, kookooIVRContext, voiceMessageType);
                break;
            }
        }
    }

    private void addToResponse(KookooIVRResponseBuilder response, KooKooIVRContext ivrContext, String messageType) {
        OutboxContext outboxContext = new OutboxContext(ivrContext);
        String patientDocId = new TAMAIVRContextFactory().create(ivrContext).patientDocumentId();
        OutboundVoiceMessage outboundVoiceMessage = voiceOutboxService.nextMessage(null, patientDocId, messageType);

        if (null != outboundVoiceMessage) {
            voiceMessageResponseFactory.voiceMessageResponse(ivrContext, outboxContext, outboundVoiceMessage, response);
            outboxContext.lastPlayedMessageId(outboundVoiceMessage.getId());
        }
    }

    private boolean hasMessageOfType(TAMAIVRContext tamaivrContext, String messageType) {
        return countMessagesOfType(tamaivrContext, messageType) != 0;
    }

    private int countMessagesOfType(TAMAIVRContext tamaivrContext, String messageType) {
        return voiceOutboxService.getNumberOfMessages(tamaivrContext.patientDocumentId(), PENDING, messageType);
    }
}
