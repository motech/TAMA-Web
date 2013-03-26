package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.command.IncomingWelcomeMessage;
import org.motechproject.tama.ivr.command.SymptomAndMessagesCommand;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuTree extends TamaDecisionTree {

    @Autowired
    IncomingWelcomeMessage incomingWelcomeMessage;

    @Autowired
    private SymptomAndMessagesCommand symptomAndMessagesCommand;

    @Autowired
    public MenuTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.MENU_TREE, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(incomingWelcomeMessage),
                        new MenuAudioPrompt().setCommand(symptomAndMessagesCommand)
                )
                .setTransitions(
                        new Object[][]{
                                {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                                {"3", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES_TREE)},
                        }
                );
    }
}
