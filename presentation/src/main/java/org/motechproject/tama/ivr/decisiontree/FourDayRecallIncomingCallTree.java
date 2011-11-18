package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.fourdayrecall.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FourDayRecallIncomingCallTree extends TamaDecisionTree {
    @Autowired
    private IncomingWelcomeGreetingMessage welcomeGreetingMessage;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(welcomeGreetingMessage),
                        new MenuAudioPrompt().setName(TamaIVRMessage.MENU_010_05_01_MAINMENU4),
                        new TextToSpeechPrompt().setName("if you want to listen to health tips Press 5")
                )
                .setTransitions(new Object[][]{
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                });
    }
}
