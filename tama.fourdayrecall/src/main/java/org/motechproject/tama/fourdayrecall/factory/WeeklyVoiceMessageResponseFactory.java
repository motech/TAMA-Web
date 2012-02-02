package org.motechproject.tama.fourdayrecall.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageFactory;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyVoiceMessageResponseFactory implements OutboxMessageFactory {

    @Autowired
    public WeeklyVoiceMessageResponseFactory(VoiceMessageResponseFactory voiceMessageResponseFactory) {
        voiceMessageResponseFactory.registerOutboxFactory(CallPreference.FourDayRecall, this);
    }

    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC);
    }
}
