package org.motechproject.tama.symptomreporting.context;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.context.SymptomModuleStratergy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.symptomreporting.decisiontree.SymptomReportingTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTreeStratergy extends SymptomModuleStratergy {

    private TAMATreeRegistry tamaTreeRegistry;

    @Autowired
    public SymptomReportingTreeStratergy(TAMACallFlowController tamaCallFlowController, TAMATreeRegistry tamaTreeRegistry) {
        super(tamaCallFlowController);
        this.tamaTreeRegistry = tamaTreeRegistry;
    }

    @Override
    public Tree getTree(String treeName, TAMAIVRContext tamaivrContext) {
        SymptomReportingTree tamaDecisionTree = (SymptomReportingTree) tamaTreeRegistry.getDecisionTrees().get(treeName);
        return tamaDecisionTree.getTree(tamaivrContext.symptomReportingTree());
    }
}
