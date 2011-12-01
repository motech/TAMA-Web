package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.command.DisableOutboxCallRetryCommand;
import org.motechproject.tamacallflow.ivr.command.CallStateCommand;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.ivr.command.WelcomeMessageForOutboundCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxCallTree extends TamaDecisionTree {
    @Autowired
    private WelcomeMessageForOutboundCall welcomeMessageForOutboundCall;

    @Autowired
    private DisableOutboxCallRetryCommand disableOutboxCallRetryCommand;

    @Override
    protected Node createRootNode() {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();

        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(welcomeMessageForOutboundCall),
                        new AudioPrompt().setName(TamaIVRMessage.FILE_050_03_01_ITS_TIME_FOR_BEST_CALL_TIME)
                )
                .setTreeCommands(new CallStateCommand(CallState.OUTBOX, contextFactory),
                        disableOutboxCallRetryCommand);
    }
}
