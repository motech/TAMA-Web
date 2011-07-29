package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.web.command.MessageFromPreviousDosage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PreviousDosageTree extends TAMADecisionTree {

    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;

    //TODO: Please talk to Vivek/Prateek before implementing this tree
    //TODO: Here transitions will be conditional which is not implemented yet. look at the Call flow
    @Override
    protected Node createRootNode() {
        return Node.newBuilder()
                .setTransitions(new Object[][]{
                }).build();
    }
}
