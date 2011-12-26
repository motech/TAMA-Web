package org.motechproject.tama.outbox.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VoiceMessageResponseFactory {

    private AllPatients allPatients;
    private Map<CallPreference, OutboxMessageFactory> outboxMessageFactoryMap;

    @Autowired
    public VoiceMessageResponseFactory(AllPatients allPatients) {
        this.allPatients = allPatients;
        outboxMessageFactoryMap = new HashMap<CallPreference, OutboxMessageFactory>();
    }

    public void registerOutboxFactory(CallPreference callPreference, OutboxMessageFactory outboxMessageFactory) {
        outboxMessageFactoryMap.put(callPreference, outboxMessageFactory);
    }

    public void voiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        CallPreference callPreference = allPatients.get(outboxContext.partyId()).callPreference();
        outboxMessageFactoryMap.get(callPreference).buildVoiceMessageResponse(kooKooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
    }
}
