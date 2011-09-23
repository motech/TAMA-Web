package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.server.service.ivr.IVRContext;
import org.springframework.stereotype.Component;

@Component
public class Regimen1To6Tree extends TamaDecisionTree {

	@Override
	protected Node createRootNode(IVRContext ivrContext) {
		try {
			Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen1To6TreeBuilder");
			return (Node)builderClass.getMethod("getRootNode", new Class[0]).invoke(null);
		} catch (Exception e) {
			return null;
		}
	}
}
