package org.motechproject.tama.messages.provider;

import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AdherenceTrendMessage;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.today;

@Component
public class AdherenceTrendMessageProvider implements MessageProvider {

    private PatientOnCall patientOnCall;
    private PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;

    @Autowired
    public AdherenceTrendMessageProvider(PatientOnCall patientOnCall, PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand) {
        this.patientOnCall = patientOnCall;
        this.playAdherenceTrendFeedbackCommand = playAdherenceTrendFeedbackCommand;
    }

    @Override
    public boolean hasMessage(TAMAIVRContext context) {
        LocalDate today = today();
        return message(context).isValid(today);
    }

    @Override
    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context) {
        AdherenceTrendMessage message = message(context);
        context.setTAMAMessageType(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        context.lastPlayedMessageId(message.getId());
        return message.build(context);
    }

    private AdherenceTrendMessage message(TAMAIVRContext context) {
        TreatmentAdvice advice = patientOnCall.getCurrentTreatmentAdvice(context);
        return new AdherenceTrendMessage(advice, playAdherenceTrendFeedbackCommand);
    }
}
