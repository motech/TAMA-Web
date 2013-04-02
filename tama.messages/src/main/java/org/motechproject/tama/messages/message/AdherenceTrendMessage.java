package org.motechproject.tama.messages.message;


import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

public class AdherenceTrendMessage {

    private TreatmentAdvice advice;
    private PlayAdherenceTrendFeedbackCommand command;


    public AdherenceTrendMessage(TreatmentAdvice advice, PlayAdherenceTrendFeedbackCommand command) {
        this.advice = advice;
        this.command = command;
    }

    public boolean isValid(LocalDate reference) {
        return advice.hasAdherenceTrend(reference);
    }

    public String getId() {
        return advice.getId();
    }

    public KookooIVRResponseBuilder build(TAMAIVRContext context) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(context.callId());
        return response.withPlayAudios(command.executeCommand(context));
    }
}
