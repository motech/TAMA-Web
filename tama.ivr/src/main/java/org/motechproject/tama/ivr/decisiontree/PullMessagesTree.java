package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.MessageTransitionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.tama.common.domain.TAMAMessageType.*;

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
                                return new String[]{TamaIVRMessage.MESSAGES_MENU};
                            }
                        })
                )
                .setTransitions(
                        new Object[][]{
                                {"1", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, ALL_MESSAGES.name())},
                                {"2", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, FAMILY_AND_CHILDREN.name())},
                                {"3", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, NUTRITION_AND_LIFESTYLE.name())},
                                {"4", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, SYMPTOMS.name())},
                                {"5", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, ADHERENCE_TO_ART.name())},
                                {"6", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, ART_AND_CD4.name())},
                                {"7", MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, LIVING_WITH_HIV.name())},

                                {"9", TAMATransitionFactory.createResetTransition()},
                        }
                );
    }
}
