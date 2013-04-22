package org.motechproject.tama.messages.provider;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.service.MessageTrackingService;

import java.util.List;


public class MessageProviders {

    private List<MessageProvider> messageProviders;
    private MessageTrackingService messageTrackingService;

    public MessageProviders(List<MessageProvider> messageProviders, MessageTrackingService messageTrackingService) {
        this.messageProviders = messageProviders;
        this.messageTrackingService = messageTrackingService;
    }

    public boolean hasAnyMessage(Method method, TAMAIVRContext context, TAMAMessageType type) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(method, context, type)) {
                return true;
            }
        }
        return false;
    }

    public KookooIVRResponseBuilder getResponse(Method method, TAMAIVRContext context, TAMAMessageType type) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(method, context, type)) {
                return messageProvider.nextMessage(context);
            }
        }
        return new KookooIVRResponseBuilder().language(context.preferredLanguage()).withSid(context.callId());
    }

    public void markAsRead(Method method, String messageType, String messageId) {
        messageTrackingService.markAsRead(method, messageType, messageId);
    }
}
