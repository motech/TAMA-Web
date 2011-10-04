package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingTransitionsUtility {

	@Autowired
	Regimen1To6Tree regimen1To6Tree;
	public Transition newInstance() {
		return new Transition().setDestinationNode(regimen1To6Tree.getTree(null).getRootNode());
	}
}
