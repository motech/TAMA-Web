package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.command.SymptomAndOutboxMenuCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuTree extends TamaDecisionTree {

    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)}
                }
                );
    }
}
