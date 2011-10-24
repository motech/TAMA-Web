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
        try {
            Tree symtomReportingTree = RegimenTreeChooser.getTree(symptomReportingTreeName);
            service.addAlerts(symtomReportingTree.getRootNode());
            return symtomReportingTree;
        } catch (Exception e) {
            logger.error("Error in getting appropriate tree", e);
            return null;
        }
    }

    @Override
    protected Node createRootNode() {
        // no op
        return null;
    }
}