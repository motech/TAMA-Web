package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
<<<<<<< HEAD
import org.motechproject.server.service.ivr.IVRContext;
=======
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> Sushant, Manohar | #75 | Preliminary implementation of Alerts
import org.springframework.stereotype.Component;

@Component
public class Regimen1To6Tree extends TamaDecisionTree {

    @Autowired
    private SymptomReportingAlertService service;

	@Override
	protected Node createRootNode(IVRContext ivrContext) {
		try {
			Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen1To6TreeBuilder");
            final Node rootNode = (Node) builderClass.getMethod("getRootNode", new Class[0]).invoke(null);
            service.addAlerts(rootNode);
            return rootNode;
		} catch (Exception e) {
			return null;
		}
	}
}
