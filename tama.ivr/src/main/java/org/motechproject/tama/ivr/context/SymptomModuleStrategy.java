package org.motechproject.tama.ivr.context;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;

public abstract class SymptomModuleStrategy {

    public SymptomModuleStrategy(TAMACallFlowController tamaCallFlowController) {
        tamaCallFlowController.registerSymptomModule(this);
    }

    public abstract Tree getTree(String treeName, TAMAIVRContext tamaivrContext);
}
