package org.motechproject.tama.ivr.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTree extends TamaDecisionTree {

    private SymptomReportingService service;

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    public SymptomReportingTree(SymptomReportingService service) {
        this.service = service;
    }

    public Tree getTree(String symptomReportingTreeName) {

        try {
            Class symptomReportingTreeChooser = Class.forName("org.motechproject.tama.ivr.decisiontree.RegimenTreeChooser");
            Tree symptomReportingTree = (Tree) symptomReportingTreeChooser.getMethod("getTree", String.class).invoke(null, symptomReportingTreeName);
            service.addCommands(symptomReportingTree.getRootNode());
            return symptomReportingTree;
        } catch (Exception e) {
            logger.error("Error in getting appropriate tree - " + symptomReportingTreeName);
            return null;
        }
    }

    @Override
    protected Node createRootNode() {
        // no op
        return null;
    }
}