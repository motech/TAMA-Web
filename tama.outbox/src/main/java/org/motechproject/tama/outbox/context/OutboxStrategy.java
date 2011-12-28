package org.motechproject.tama.outbox.context;

import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxStrategy extends OutboxModuleStrategy {

    private AllPatients allPatients;
    private VoiceOutboxService voiceOutboxService;

    @Autowired
    public OutboxStrategy(AllPatients allPatients, TAMACallFlowController tamaCallFlowController, SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand, VoiceOutboxService voiceOutboxService) {
        super(tamaCallFlowController, symptomAndOutboxMenuCommand);
        this.allPatients = allPatients;
        this.voiceOutboxService = voiceOutboxService;
    }

    @Override
    public boolean hasOutboxCompleted(TAMAIVRContext tamaivrContext) {
        OutboxContext outboxContext = new OutboxContext(tamaivrContext.getKooKooIVRContext());
        return outboxContext.hasOutboxCompleted();
    }

    @Override
    public boolean hasPendingOutboxMessages(String patientId) {
        Patient patient = allPatients.get(patientId);
        return patient.isOnDailyPillReminder() && voiceOutboxService.getNumberPendingMessages(patientId) != 0;
    }
}
