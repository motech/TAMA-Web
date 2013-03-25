package org.motechproject.tama.messages.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.push.Messages;

public class PullMessagesController implements PatientMessagesController {

    private Messages messages;

    public PullMessagesController(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean markAsRead(KooKooIVRContext kooKooIVRContext) {
        PlayedMessage playedMessage = new PlayedMessage(kooKooIVRContext);
        if (playedMessage.exists()) {
            messages.markAsRead(kooKooIVRContext, playedMessage);
        }
        return true;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (StringUtils.equals("9", tamaivrContext.dtmfInput())) {
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        } else {
            return messages.nextMessage(kooKooIVRContext);
        }
    }
}
