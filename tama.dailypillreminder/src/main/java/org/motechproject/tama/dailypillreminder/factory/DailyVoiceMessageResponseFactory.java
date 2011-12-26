package org.motechproject.tama.dailypillreminder.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageCommand;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageFactory;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DailyVoiceMessageResponseFactory implements OutboxMessageFactory {

    private PlayAdherenceTrendFeedbackCommand adherenceTrendCommand;
    private AdherenceMessageCommand adherenceMessageCommand;

    @Autowired
    public DailyVoiceMessageResponseFactory(VoiceMessageResponseFactory voiceMessageResponseFactory, PlayAdherenceTrendFeedbackCommand command, @Qualifier("adherenceMessageCommand") AdherenceMessageCommand adherenceMessageCommand) {
        this.adherenceTrendCommand = command;
        this.adherenceMessageCommand = adherenceMessageCommand;
        voiceMessageResponseFactory.registerOutboxFactory(CallPreference.DailyPillReminder, this);
    }

    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        VoiceMessageType voiceMessageType = outboundVoiceMessage.getVoiceMessageType();
        if (voiceMessageType != null && TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO.equals(voiceMessageType.getVoiceMessageTypeName())) {
            ivrResponseBuilder.withPlayAudios(adherenceMessageCommand.execute(kooKooIVRContext));
            ivrResponseBuilder.withPlayAudios(adherenceTrendCommand.execute(outboxContext.partyId()));
        }
    }
}
