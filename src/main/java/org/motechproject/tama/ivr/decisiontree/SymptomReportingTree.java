package org.motechproject.tama.ivr.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTree extends TamaDecisionTree {
    @Autowired
    private SymptomReportingAlertService service;

    private Logger logger = Logger.getLogger(this.getClass());

    public Tree getTree(String symptomReportingTreeName) {
        try {
            Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen1To6TreeBuilder");
            Tree symtomReportingTree = (Tree) builderClass.getMethod("getTree", new Class[0]).invoke(symptomReportingTreeName);
            service.addAlerts(symtomReportingTree.getRootNode());
            return symtomReportingTree;
        } catch (Exception e) {
            logger.error("Error in getting appropriate tree", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Node createRootNode() {
        // no op
        return null;
    }
}