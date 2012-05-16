package org.motechproject.tama.clinicvisits.builder;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentReminderMessageBuilder implements OutboxMessageBuilder {

    private AllPatients allPatients;

    @Autowired
    public AppointmentReminderMessageBuilder(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        VoiceMessageType voiceMessageType = voiceMessage.getVoiceMessageType();
        return voiceMessageType != null && TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE.equals(voiceMessageType.getVoiceMessageTypeName());
    }

    @Override
    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        String clinicPhoneNumber = allPatients.get(outboundVoiceMessage.getPartyId()).getClinic().getPhone();
        List<String> allNumberFileNames = TamaIVRMessage.getAllNumberFileNames("0" + clinicPhoneNumber);

        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NEXT_CLINIC_VISIT_IS_DUE_PART1);
        ivrResponseBuilder.withPlayAudios(allNumberFileNames.toArray(new String[allNumberFileNames.size()]));
        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NEXT_CLINIC_VISIT_IS_DUE_PART2);
    }
}
