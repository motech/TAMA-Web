package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.service.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushMessagesController implements PatientMessagesController {

    private Messages messages;

    @Autowired
    public PushMessagesController(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean markAsReadAndContinue(KooKooIVRContext kooKooIVRContext) {
        PlayedMessage playedMessage = new PlayedMessage(kooKooIVRContext);
        if (playedMessage.exists()) {
            messages.markAsRead(kooKooIVRContext, playedMessage);
            return false;
        }
        return true;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        return messages.nextMessage(kooKooIVRContext);
    }
}


