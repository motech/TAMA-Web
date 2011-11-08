package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.outbox.OutboxContext;
import org.motechproject.tama.web.command.MessageForAdherenceWhenPreviousDosageNotCapturedCommand;
import org.motechproject.tama.web.command.PlayAdherenceTrendFeedbackCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoiceMessageResponseFactory {
    private PlayAdherenceTrendFeedbackCommand adherenceTrendCommand;
    private MessageForAdherenceWhenPreviousDosageNotCapturedCommand adherencePercentageCommand;

    @Autowired
    public VoiceMessageResponseFactory(PlayAdherenceTrendFeedbackCommand command, MessageForAdherenceWhenPreviousDosageNotCapturedCommand adherencePercentageCommand) {
        this.adherenceTrendCommand = command;
        this.adherencePercentageCommand = adherencePercentageCommand;
    }

    public void voiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        VoiceMessageType voiceMessageType = outboundVoiceMessage.getVoiceMessageType();
        if (voiceMessageType != null && "AudioCommand".equals(voiceMessageType.getVoiceMessageTypeName())) {
            ivrResponseBuilder.withPlayAudios(adherencePercentageCommand.execute(kooKooIVRContext));
            ivrResponseBuilder.withPlayAudios(adherenceTrendCommand.execute(outboxContext));
        }
    }
}
