package org.motechproject.tama.outbox.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.CallStateCommand;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.outbox.command.DisableOutboxCallRetryCommand;
import org.motechproject.tama.outbox.command.WelcomeMessageForOutboundCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxCallTree extends TamaDecisionTree {
    @Autowired
    private WelcomeMessageForOutboundCall welcomeMessageForOutboundCall;

    @Autowired
    private DisableOutboxCallRetryCommand disableOutboxCallRetryCommand;

    @Autowired
    public OutboxCallTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.OUTBOX_CALL, this);
    }

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
