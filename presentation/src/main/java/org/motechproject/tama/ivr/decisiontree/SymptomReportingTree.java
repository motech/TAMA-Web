package org.motechproject.tama.ivr.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTree extends TamaDecisionTree {

    private SymptomReportingTreeInterceptor interceptor;

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    public SymptomReportingTree(SymptomReportingTreeInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public Tree getTree(String symptomReportingTreeName) {

        try {
            Class symptomReportingTreeChooser = Class.forName("org.motechproject.tama.ivr.decisiontree.RegimenTreeChooser");
            Tree symptomReportingTree = (Tree) symptomReportingTreeChooser.getMethod("getTree", String.class).invoke(null, symptomReportingTreeName);
            interceptor.addCommands(symptomReportingTree.getRootNode());
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