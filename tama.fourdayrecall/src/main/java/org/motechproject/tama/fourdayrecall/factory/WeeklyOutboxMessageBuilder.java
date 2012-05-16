package org.motechproject.tama.fourdayrecall.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyOutboxMessageBuilder implements OutboxMessageBuilder {

    private AllPatients allPatients;

    @Autowired
    public WeeklyOutboxMessageBuilder(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        VoiceMessageType voiceMessageType = voiceMessage.getVoiceMessageType();

        if (!allPatients.get(voiceMessage.getExternalId()).isOnWeeklyPillReminder()) {
            return false;
        }
        return voiceMessageType != null && TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO.equals(voiceMessageType.getVoiceMessageTypeName());
    }

    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        ivrResponseBuilder.withPlayTexts("This is your outbox message");
    }
}
