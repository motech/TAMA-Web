package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuTree extends TamaDecisionTree {

    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    @Autowired
    public MenuTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.MENU_TREE, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand),
                        new MenuAudioPrompt().setName(TamaIVRMessage.HEALTH_TIPS_MENU_OPTION))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransitionWithAudio(CallState.SYMPTOM_REPORTING, TamaIVRMessage.START_SYMPTOM_FLOW)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                }
                );
    }
}
