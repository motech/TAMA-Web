package org.motechproject.tama.messages.message;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.AdherenceTrendAudios;
import org.motechproject.tama.messages.service.AdherenceTrendService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceTrendMessage {

    private AdherenceTrendService adherenceTrendService;

    @Autowired
    public AdherenceTrendMessage(AdherenceTrendService adherenceTrendService) {
        this.adherenceTrendService = adherenceTrendService;
    }

    public boolean isValid(TreatmentAdvice advice, LocalDate reference) {
        return advice.hasAdherenceTrend(reference);
    }

    public String getId(TreatmentAdvice advice) {
        return advice.getId();
    }

    public KookooIVRResponseBuilder build(Patient patient, DateTime dateTime, TAMAIVRContext context) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(context.callId());
        double adherencePercentage = adherenceTrendService.getAdherencePercentage(patient, dateTime);
        boolean falling = adherenceTrendService.isAdherenceFalling(patient, dateTime);
        return response.withPlayAudios(new AdherenceTrendAudios(adherencePercentage, falling).getFiles());
    }
}
