package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.CallStateCommand;
import org.motechproject.tama.web.command.fourdayrecall.WelcomeGreetingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxCallTree extends TamaDecisionTree {
    @Autowired
    private WelcomeGreetingMessage welcomeGreetingMessage;

    @Override
    protected Node createRootNode() {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();

        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(welcomeGreetingMessage),
                        new AudioPrompt().setName(TamaIVRMessage.FILE_050_03_01_ITS_TIME_FOR_BEST_CALL_TIME)
                )
                .setTreeCommands(new CallStateCommand(CallState.OUTBOX, contextFactory));
    }
}
