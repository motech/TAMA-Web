package org.motechproject.tama.ivr.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTree extends TamaDecisionTree {

    private SymptomReportingAlertService service;

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    public SymptomReportingTree(SymptomReportingAlertService service) {
        this.service = service;
    }

    public Tree getTree(String symptomReportingTreeName) {
        Tree symptomReportingTree = RegimenTreeChooser.getTree(symptomReportingTreeName);
        if (symptomReportingTree == null) {
            return null;
        }
        service.addAlerts(symptomReportingTree.getRootNode());
        return symptomReportingTree;
    }

    @Override
    protected Node createRootNode() {
        // no op
        return null;
    }
}