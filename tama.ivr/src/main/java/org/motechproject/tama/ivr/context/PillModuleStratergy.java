package org.motechproject.tama.ivr.context;

import org.motechproject.tama.ivr.controller.TAMACallFlowController;

public abstract class PillModuleStratergy {

    public PillModuleStratergy(TAMACallFlowController tamaCallFlowController) {
        tamaCallFlowController.registerPillModule(this);
    }

    public abstract boolean previousDosageCaptured(TAMAIVRContext tamaivrContext);

    public abstract boolean isCurrentDoseTaken(TAMAIVRContext tamaivrContext);
}
