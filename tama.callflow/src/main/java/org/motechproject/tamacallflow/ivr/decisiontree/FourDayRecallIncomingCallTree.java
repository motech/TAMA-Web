package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.command.SymptomAndOutboxMenuCommand;
import org.motechproject.tamacallflow.ivr.command.fourdayrecall.IncomingWelcomeGreetingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FourDayRecallIncomingCallTree extends TamaDecisionTree {
    @Autowired
    private IncomingWelcomeGreetingMessage welcomeGreetingMessage;
    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(welcomeGreetingMessage),
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand),
                        new TextToSpeechPrompt().setName("if you want to listen to health tips Press 5"))
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                });
    }
}
