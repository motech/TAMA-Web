package org.motechproject.tama.messages.push;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Messages {

    private OutboxMessage outboxMessage;
    private HealthTipMessage healthTipMessage;
    private PatientService patientService;

    @Autowired
    public Messages(OutboxMessage outboxMessage, HealthTipMessage healthTipMessage, PatientService patientService) {
        this.outboxMessage = outboxMessage;
        this.healthTipMessage = healthTipMessage;
        this.patientService = patientService;
    }

    public KookooIVRResponseBuilder nextMessage(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kooKooIVRContext);
        KookooIVRResponseBuilder response = defaultMessageForPatientOnWeeklyReminder(context);
        List<String> audios = messages(kooKooIVRContext).getPlayAudios();
        response.withPlayAudios(audios.toArray(new String[audios.size()]));
        return response;
    }

    public void markAsRead(KooKooIVRContext kookooIVRContext, PlayedMessage playedMessage) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kookooIVRContext);
        if (PlayedMessage.Types.HEALTH_TIPS.equals(playedMessage.type())) {
            healthTipMessage.markAsRead(context.patientDocumentId(), playedMessage.id());
        } else {
            outboxMessage.markAsRead(kookooIVRContext);
        }
    }

    private KookooIVRResponseBuilder defaultMessageForPatientOnWeeklyReminder(TAMAIVRContext context) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(context.callId());
        if (patientService.getPatientReport(context.patientDocumentId()).getPatient().isOnWeeklyPillReminder()) {
            response.withPlayAudios(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY);
        }
        return response;
    }

    private KookooIVRResponseBuilder messages(KooKooIVRContext kooKooIVRContext) {
        if (outboxMessage.hasAnyMessage(kooKooIVRContext)) {
            return outboxMessage.getResponse(kooKooIVRContext);
        } else if (healthTipMessage.hasAnyMessage(kooKooIVRContext)) {
            return healthTipMessage.getResponse(kooKooIVRContext);
        } else {
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        }
    }
}
