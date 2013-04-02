package org.motechproject.tama.messages.provider;

import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AdherenceTrendMessage;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.today;

@Component
public class AdherenceTrendMessageProvider implements MessageProvider {

    private PatientOnCall patientOnCall;
    private AdherenceTrendMessage adherenceTrendMessage;

    @Autowired
    public AdherenceTrendMessageProvider(PatientOnCall patientOnCall, AdherenceTrendMessage adherenceTrendMessage) {
        this.patientOnCall = patientOnCall;
        this.adherenceTrendMessage = adherenceTrendMessage;
    }

    @Override
    public boolean hasMessage(TAMAIVRContext context) {
        LocalDate today = today();
        TreatmentAdvice advice = patientOnCall.getCurrentTreatmentAdvice(context);
        return adherenceTrendMessage.isValid(advice, today);
    }

    @Override
    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context) {
        TreatmentAdvice advice = patientOnCall.getCurrentTreatmentAdvice(context);
        AdherenceTrendMessage message = adherenceTrendMessage;
        prepareContext(context, advice, message);
        return message.build(patientOnCall.getPatient(context), DateUtil.now(), context);
    }

    private void prepareContext(TAMAIVRContext context, TreatmentAdvice advice, AdherenceTrendMessage message) {
        context.setTAMAMessageType(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        context.lastPlayedMessageId(message.getId(advice));
    }
}
