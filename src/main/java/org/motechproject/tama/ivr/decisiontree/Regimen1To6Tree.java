package org.motechproject.tama.ivr.decisiontree;

import org.apache.log4j.Logger;
import org.motechproject.decisiontree.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Regimen1To6Tree extends TamaDecisionTree {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SymptomReportingAlertService service;

    @Override
    protected Node createRootNode() {
        try {
            Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen1To6TreeBuilder");
            final Node rootNode = (Node) builderClass.getMethod("getRootNode", new Class[0]).invoke(null);
            service.addAlerts(rootNode);
            return null;
        } catch (Exception e) {
            logger.error("Error in getting appropriate tree", e);
            e.printStackTrace();
            return null;
        }
    }
}
