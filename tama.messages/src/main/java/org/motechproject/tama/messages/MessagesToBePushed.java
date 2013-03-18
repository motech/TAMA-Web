package org.motechproject.tama.messages;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.domain.PushedMessage;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessagesToBePushed {

    private PushedOutboxMessage pushedOutboxMessage;
    private PushedHealthTipMessage pushedHealthTipMessage;
    private PatientService patientService;

    @Autowired
    public MessagesToBePushed(PushedOutboxMessage pushedOutboxMessage, PushedHealthTipMessage pushedHealthTipMessage, PatientService patientService) {
        this.pushedOutboxMessage = pushedOutboxMessage;
        this.pushedHealthTipMessage = pushedHealthTipMessage;
        this.patientService = patientService;
    }

    public KookooIVRResponseBuilder nextMessage(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kooKooIVRContext);
        KookooIVRResponseBuilder response = defaultMessageForPatientOnWeeklyReminder(context);
        List<String> audios = messages(kooKooIVRContext).getPlayAudios();
        response.withPlayAudios(audios.toArray(new String[audios.size()]));
        return response;
    }

    public void markAsRead(KooKooIVRContext kookooIVRContext, PushedMessage pushedMessage) {
        TAMAIVRContext context = new TAMAIVRContextFactory().create(kookooIVRContext);
        if (PushedMessage.Types.HEALTH_TIPS.equals(pushedMessage.type())) {
            pushedHealthTipMessage.markAsRead(context.patientDocumentId(), pushedMessage.id());
        } else {
            pushedOutboxMessage.markAsRead(kookooIVRContext);
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
        if (pushedOutboxMessage.hasAnyMessage(kooKooIVRContext)) {
            return pushedOutboxMessage.getResponse(kooKooIVRContext);
        } else if (pushedHealthTipMessage.hasAnyMessage(kooKooIVRContext)) {
            return pushedHealthTipMessage.getResponse(kooKooIVRContext);
        } else {
            return new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        }
    }
}
