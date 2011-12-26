package org.motechproject.tama.ivr.context;

import org.motechproject.tama.ivr.controller.TAMACallFlowController;

public abstract class PillModuleStrategy {

    public PillModuleStrategy(TAMACallFlowController tamaCallFlowController) {
        tamaCallFlowController.registerPillModule(this);
    }

    public abstract boolean previousDosageCaptured(TAMAIVRContext tamaivrContext);

    public abstract boolean isCurrentDoseTaken(TAMAIVRContext tamaivrContext);
}
