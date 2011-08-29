package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.springframework.stereotype.Component;

@Component
public class Regimen6Tree extends TamaDecisionTree {

	@Override
	protected Node createRootNode() {
		try {
			Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen6TreeBuilder");
			return (Node)builderClass.getMethod("getRootNode", new Class[0]).invoke(null);
		} catch (Exception e) {
			return null;
		}
	}
}
