package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.push.Messages;

public class PushMessagesController implements PatientMessagesController {

    private Messages messages;

    public PushMessagesController(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean markAsRead(KooKooIVRContext kooKooIVRContext) {
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


