package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
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
                        new MenuAudioPrompt().setAudioFileUrl("AudioFile")
                )
                .setTransitions(
                        new Object[][]{
                                {"9", TAMATransitionFactory.createResetTransition()},
                        }
                );
    }
}
