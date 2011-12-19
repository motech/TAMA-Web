package org.motechproject.tama.outbox.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageCommand;
import org.motechproject.tama.outbox.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VoiceMessageResponseFactory {

    private PlayAdherenceTrendFeedbackCommand adherenceTrendCommand;
    private AdherenceMessageCommand adherenceMessageCommand;

    @Autowired
    public VoiceMessageResponseFactory(PlayAdherenceTrendFeedbackCommand command, @Qualifier("adherenceMessageCommand") AdherenceMessageCommand adherenceMessageCommand) {
        this.adherenceTrendCommand = command;
        this.adherenceMessageCommand = adherenceMessageCommand;
    }

    public void voiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        VoiceMessageType voiceMessageType = outboundVoiceMessage.getVoiceMessageType();
        if (voiceMessageType != null && "AudioCommand".equals(voiceMessageType.getVoiceMessageTypeName())) {
            ivrResponseBuilder.withPlayAudios(adherenceMessageCommand.execute(kooKooIVRContext));
            ivrResponseBuilder.withPlayAudios(adherenceTrendCommand.execute(outboxContext));
        }
    }
}
