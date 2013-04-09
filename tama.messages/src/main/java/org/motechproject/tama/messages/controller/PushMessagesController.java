package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.service.Messages;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PushMessagesController implements PatientMessagesController {

    private Messages messages;
    private PatientService patientService;

    @Autowired
    public PushMessagesController(Messages messages, PatientService patientService) {
        this.messages = messages;
        this.patientService = patientService;
    }

    @Override
    public boolean markAsReadAndContinue(KooKooIVRContext kooKooIVRContext) {
        PlayedMessage playedMessage = new PlayedMessage(kooKooIVRContext);
        if (playedMessage.exists()) {
            new TAMAIVRContextFactory().create(kooKooIVRContext).setMessagesPushed(true);
            messages.markAsRead(kooKooIVRContext, playedMessage);
        }
        return false;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {

        TAMAIVRContext context = new TAMAIVRContextFactory().create(kooKooIVRContext);
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(context.callId());
        return buildResponse(kooKooIVRContext, response);
    }

    private KookooIVRResponseBuilder buildResponse(KooKooIVRContext kooKooIVRContext, KookooIVRResponseBuilder response) {
        List<String> audios = messages.nextMessage(kooKooIVRContext, TAMAMessageType.PUSHED_MESSAGE).getPlayAudios();
        return response.withPlayAudios(audios.toArray(new String[audios.size()]));
    }
}


