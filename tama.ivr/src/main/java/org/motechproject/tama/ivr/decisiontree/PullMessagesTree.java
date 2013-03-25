package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PullMessagesTree extends TamaDecisionTree {


    @Autowired
    public PullMessagesTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.PULL_MESSAGES_TREE, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(new ITreeCommand() {
                            @Override
                            public String[] execute(Object o) {
                                return new String[]{"AudioFile"};
                            }
                        })
                )
                .setTransitions(
                        new Object[][]{
                                {"1", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"2", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"3", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"4", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"5", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"6", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"7", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES)},
                                {"9", TAMATransitionFactory.createResetTransition()},
                        }
                );
    }
}
