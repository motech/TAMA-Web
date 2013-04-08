package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AdherenceTrendMessage;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.tama.common.domain.TAMAMessageType.ADHERENCE_TO_ART;
import static org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE;
import static org.motechproject.util.DateUtil.now;

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
    public boolean hasMessage(TAMAIVRContext context, TAMAMessageType type) {
        DateTime now = now();
        if (PUSHED_MESSAGE.equals(type) || ADHERENCE_TO_ART.equals(type)) {
            TreatmentAdvice advice = patientOnCall.getCurrentTreatmentAdvice(context);
            Patient patient = patientOnCall.getPatient(context);
            return adherenceTrendMessage.isValid(patient, advice, now);
        } else {
            return false;
        }
    }

    @Override
    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context) {
        TreatmentAdvice advice = patientOnCall.getCurrentTreatmentAdvice(context);
        prepareContext(context, advice);
        return adherenceTrendMessage.build(patientOnCall.getPatient(context), DateUtil.now(), context);
    }

    private void prepareContext(TAMAIVRContext context, TreatmentAdvice advice) {
        context.setTAMAMessageType(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        context.lastPlayedMessageId(adherenceTrendMessage.getId(advice));
    }
}
