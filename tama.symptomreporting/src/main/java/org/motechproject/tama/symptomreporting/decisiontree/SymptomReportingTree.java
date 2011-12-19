package org.motechproject.tama.symptomreporting.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.motechproject.tama.ivr.decisiontree.service.SymptomReportingTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTree extends TamaDecisionTree {

    private SymptomReportingTreeService symptomReportingTreeService;
    private SymptomReportingTreeInterceptor symptomReportingTreeInterceptor;

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    public SymptomReportingTree(SymptomReportingTreeService symptomReportingTreeService, SymptomReportingTreeInterceptor symptomReportingTreeInterceptor, TAMATreeRegistry tamaTreeRegistry) {
        this.symptomReportingTreeService = symptomReportingTreeService;
        this.symptomReportingTreeInterceptor = symptomReportingTreeInterceptor;
        tamaTreeRegistry.register(TAMATreeRegistry.REGIMEN_1_TO_6, this);
    }

    public Tree getTree(String symptomReportingTreeName) {
        try {
            Tree symptomReportingTree = symptomReportingTreeService.getTree(symptomReportingTreeName);
            symptomReportingTreeInterceptor.addCommands(symptomReportingTree.getRootNode());
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
