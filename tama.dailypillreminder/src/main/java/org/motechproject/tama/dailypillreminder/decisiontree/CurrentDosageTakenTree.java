package org.motechproject.tama.dailypillreminder.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.dailypillreminder.command.NextCallDetails;
import org.motechproject.tama.ivr.command.IncomingWelcomeMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageTakenTree extends TamaDecisionTree {
    @Autowired
    private IncomingWelcomeMessage incomingWelcomeMessage;

    @Autowired
    private NextCallDetails nextCallDetails;

    @Autowired
    public CurrentDosageTakenTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(incomingWelcomeMessage),
                        new AudioPrompt().setCommand(nextCallDetails)
                );
    }
}
