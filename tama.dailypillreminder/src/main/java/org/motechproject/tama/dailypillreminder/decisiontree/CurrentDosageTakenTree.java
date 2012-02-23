package org.motechproject.tama.dailypillreminder.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.dailypillreminder.command.IncomingWelcomeMessage;
import org.motechproject.tama.dailypillreminder.command.NextCallDetails;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.ivr.decisiontree.TAMATransitionFactory;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageTakenTree extends TamaDecisionTree {
    @Autowired
    private IncomingWelcomeMessage incomingWelcomeMessage;

    @Autowired
    private NextCallDetails nextCallDetails;

    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    @Autowired
    public CurrentDosageTakenTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(incomingWelcomeMessage),
                        new AudioPrompt().setCommand(nextCallDetails),
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand),
                        new MenuAudioPrompt().setName(TamaIVRMessage.HEALTH_TIPS_MENU_OPTION))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                }
                );
    }
}
