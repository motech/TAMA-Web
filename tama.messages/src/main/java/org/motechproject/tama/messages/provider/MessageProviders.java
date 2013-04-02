package org.motechproject.tama.messages.provider;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageProviders {

    private List<MessageProvider> messageProviders;
    private MessageTrackingService messageTrackingService;

    @Autowired
    public MessageProviders(List<MessageProvider> messageProviders, MessageTrackingService messageTrackingService) {
        this.messageProviders = messageProviders;
        this.messageTrackingService = messageTrackingService;
    }

    public boolean hasAnyMessage(TAMAIVRContext context) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(context)) {
                return true;
            }
        }
        return false;
    }

    public KookooIVRResponseBuilder getResponse(TAMAIVRContext context) {
        for (MessageProvider messageProvider : messageProviders) {
            if (messageProvider.hasMessage(context)) {
                return messageProvider.nextMessage(context);
            }
        }
        return new KookooIVRResponseBuilder().withSid(context.callId());
    }

    public void markAsRead(String messageType, String messageId) {
        messageTrackingService.markAsRead(messageType, messageId);
    }
}
