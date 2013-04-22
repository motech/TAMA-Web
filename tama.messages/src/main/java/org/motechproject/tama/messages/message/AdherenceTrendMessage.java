package org.motechproject.tama.messages.message;


import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.AdherenceTrendAudios;
import org.motechproject.tama.messages.domain.AdherenceTrendMessageCriteria;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.tama.messages.service.AdherenceTrendService;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceTrendMessage {

    private AdherenceTrendService adherenceTrendService;
    private MessageTrackingService messageTrackingService;
    private AdherenceTrendMessageCriteria criteria;

    @Autowired
    public AdherenceTrendMessage(AdherenceTrendService adherenceTrendService, MessageTrackingService messageTrackingService) {
        this(adherenceTrendService, messageTrackingService, new AdherenceTrendMessageCriteria());
    }

    public AdherenceTrendMessage(AdherenceTrendService adherenceTrendService, MessageTrackingService messageTrackingService, AdherenceTrendMessageCriteria criteria) {
        this.adherenceTrendService = adherenceTrendService;
        this.messageTrackingService = messageTrackingService;
        this.criteria = criteria;
    }

    public boolean isValid(Patient patient, TreatmentAdvice advice, DateTime reference) {
        MessageHistory history = messageTrackingService.get(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO, getId(advice));
        double adherence = adherenceTrendService.getAdherencePercentage(patient, reference);
        return adherenceTrendService.hasAdherenceTrend(patient, advice, reference) && criteria.shouldPlay(adherence, history, reference);
    }

    public String getId(TreatmentAdvice advice) {
        return advice.getId();
    }

    public KookooIVRResponseBuilder build(Patient patient, DateTime dateTime, TAMAIVRContext context) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().language(context.preferredLanguage()).withSid(context.callId());
        double adherencePercentage = adherenceTrendService.getAdherencePercentage(patient, dateTime);
        boolean falling = adherenceTrendService.isAdherenceFalling(patient, dateTime);
        return response.withPlayAudios(new AdherenceTrendAudios(adherencePercentage, falling).getFiles());
    }
}
