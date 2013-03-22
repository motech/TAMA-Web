package org.motechproject.tama.ivr.context;

import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;

public abstract class OutboxModuleStrategy {

    public OutboxModuleStrategy(TAMACallFlowController tamaCallFlowController, SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand) {
        tamaCallFlowController.registerOutboxModule(this);
        symptomAndOutboxMenuCommand.registerOutboxModule(this);
    }

    public abstract boolean shouldContinueToOutbox(String patientDocumentId);
}
