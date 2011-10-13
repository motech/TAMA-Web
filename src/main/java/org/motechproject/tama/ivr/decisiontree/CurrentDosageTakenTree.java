package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.NextCallDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageTakenTree extends TamaDecisionTree {
    @Autowired
    private NextCallDetails nextCallDetails;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(new AudioPrompt().setCommand(nextCallDetails),
                        new AudioPrompt().setName(TamaIVRMessage.MENU_010_05_01_MAINMENU4))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)}
                }
                );
    }
}
