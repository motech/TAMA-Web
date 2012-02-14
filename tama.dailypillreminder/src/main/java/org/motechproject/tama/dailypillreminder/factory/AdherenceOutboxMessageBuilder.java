package org.motechproject.tama.dailypillreminder.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageCommand;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdherenceOutboxMessageBuilder implements OutboxMessageBuilder {

    private PlayAdherenceTrendFeedbackCommand adherenceTrendCommand;
    private AdherenceMessageCommand adherenceMessageCommand;
    private AllPatients allPatients;

    @Autowired
    public AdherenceOutboxMessageBuilder(PlayAdherenceTrendFeedbackCommand command, @Qualifier("adherenceMessageCommand") AdherenceMessageCommand adherenceMessageCommand, AllPatients allPatients) {
        this.adherenceTrendCommand = command;
        this.adherenceMessageCommand = adherenceMessageCommand;
        this.allPatients = allPatients;
    }

    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        if (patientIsNotOnDailyPillReminder(voiceMessage))
            return false;
        VoiceMessageType voiceMessageType = voiceMessage.getVoiceMessageType();
        return voiceMessageType != null && TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO.equals(voiceMessageType.getVoiceMessageTypeName());
    }

    private boolean patientIsNotOnDailyPillReminder(OutboundVoiceMessage voiceMessage) {
        return !allPatients.get(voiceMessage.getPartyId()).callPreference().isDaily();
    }

    @Override
    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        ivrResponseBuilder.withPlayAudios(adherenceMessageCommand.execute(kooKooIVRContext));
        ivrResponseBuilder.withPlayAudios(adherenceTrendCommand.execute(outboxContext.partyId()));
    }
}
