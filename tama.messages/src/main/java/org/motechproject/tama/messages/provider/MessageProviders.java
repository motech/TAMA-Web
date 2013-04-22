package org.motechproject.tama.messages.provider;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.service.MessageTrackingService;

import java.util.List;


public class MessageProviders {

    private List<MessageProvider> messageProviders;
    private MessageTrackingService messageTrackingService;

    public MessageProviders(List<MessageProvider> messageProviders, MessageTrackingService messageTrackingService) {
        this.messageProviders = messageProviders;
        this.messageTrackingService = messageTrackingService;
    }

    public boolean hasAnyMessage(TAMAIVRContext context, TAMAMessageType type) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(context, type)) {
                return true;
            }
        }
        return false;
    }

    public KookooIVRResponseBuilder getResponse(TAMAIVRContext context, TAMAMessageType type) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(context, type)) {
                return messageProvider.nextMessage(context);
            }
        }
        return new KookooIVRResponseBuilder().language(context.preferredLanguage()).withSid(context.callId());
    }

    public void markAsRead(String messageType, String messageId) {
        messageTrackingService.markAsRead(messageType, messageId);
    }
}
