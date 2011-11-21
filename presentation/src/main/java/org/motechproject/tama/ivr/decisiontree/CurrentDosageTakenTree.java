package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.web.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tama.web.command.NextCallDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageTakenTree extends TamaDecisionTree {
    @Autowired
    private NextCallDetails nextCallDetails;

    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(nextCallDetails),
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand),
                        new TextToSpeechPrompt().setName("if you want to listen to health tips Press 5"))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                }
                );
    }
}