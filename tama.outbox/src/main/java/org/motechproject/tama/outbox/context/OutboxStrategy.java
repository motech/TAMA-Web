package org.motechproject.tama.outbox.context;

import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.outbox.service.OutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxStrategy extends OutboxModuleStrategy {

    private OutboxService outboxService;

    @Autowired
    public OutboxStrategy(TAMACallFlowController tamaCallFlowController, SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand, OutboxService outboxService) {
        super(tamaCallFlowController, symptomAndOutboxMenuCommand);
        this.outboxService = outboxService;
    }

    @Override
    public boolean hasOutboxCompleted(TAMAIVRContext tamaivrContext) {
        OutboxContext outboxContext = new OutboxContext(tamaivrContext.getKooKooIVRContext());
        return outboxContext.hasOutboxCompleted();
    }

    @Override
    public boolean shouldContinueToOutbox(String patientDocumentId) {
        return outboxService.hasPendingOutboxMessages(patientDocumentId);
    }
}
