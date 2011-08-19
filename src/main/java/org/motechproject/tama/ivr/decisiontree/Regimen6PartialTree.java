package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Regimen6PartialTree extends TamaDecisionTree {

	@Override
	protected Node createRootNode() {
		try {
			Class builderClass = Class.forName("org.motechproject.tama.ivr.decisiontree.Regimen6PartialTreeBuilder");
			return (Node)builderClass.getMethod("getRootNode", Void.class).invoke(null);
			
		} catch (Exception e) {
			return null;
		}
	}
}
