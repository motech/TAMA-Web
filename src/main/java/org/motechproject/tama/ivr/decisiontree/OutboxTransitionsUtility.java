package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.URLTransition;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxTransitionsUtility {
	
	@Autowired
	IVRMessage ivrMessage;
	
	public Transition newInstance() {
		return new Transition().setDestinationNode(
        	new Node().setTransitions(new Object[][]{{"", new URLTransition(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL)) }}
        ));
	}
}
