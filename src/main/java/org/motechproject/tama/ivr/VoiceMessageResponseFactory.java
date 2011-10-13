package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.outbox.OutboxContext;
import org.motechproject.tama.web.OutboxController;
import org.motechproject.tama.web.command.WeeklyAdherenceOutBoxCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VoiceMessageResponseFactory {
    private WeeklyAdherenceOutBoxCommand command;

    @Autowired
    public VoiceMessageResponseFactory(WeeklyAdherenceOutBoxCommand command) {
        this.command = command;
    }

    public void voiceMessageResponse(OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        VoiceMessageType voiceMessageType = outboundVoiceMessage.getVoiceMessageType();
        if (voiceMessageType != null && "AudioCommand".equals(voiceMessageType.getVoiceMessageTypeName())) {
            List<String> commands = (List<String>) outboundVoiceMessage.getParameters().get(OutboxController.VOICE_MESSAGE_COMMAND);
            for (String s : commands) {
                ivrResponseBuilder.withPlayAudios(command.execute(outboxContext));
            }
        }
    }

}
