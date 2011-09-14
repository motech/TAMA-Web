package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PreviousDosageReminderTree extends TamaDecisionTree {
    @Autowired
    private StopPreviousPillReminderCommand stopPreviousPillReminderCommand;
    @Autowired
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;
    @Autowired
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;
    @Autowired
    private MessageForAdherenceWhenPreviousDosageNotCapturedCommand messageForAdherenceWhenPreviousDosageNotCapturedCommand;
    @Autowired
    private UpdatePreviousPillAdherenceCommand updatePreviousPillAdherenceCommand;

    @Override
    protected Node createRootNode() {
        return new Node().setTransitions(new Object[][]{
                {"1", new Transition()
                        .setDestinationNode(
                                new Node()
                                        .setTreeCommands(stopPreviousPillReminderCommand, updatePreviousPillAdherenceCommand)
                                        .setPrompts(
                                                new AudioPrompt().setCommand(messageOnPreviousPillTaken),
                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageNotCapturedCommand)
                                        )
                        )

                },
                {"3", new Transition()
                        .setDestinationNode(
                                new Node()
                                        .setTreeCommands(stopPreviousPillReminderCommand, updatePreviousPillAdherenceCommand)
                                        .setPrompts(
                                                new AudioPrompt().setCommand(messageOnPreviousPillNotTaken),
                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageNotCapturedCommand)
                                        )
                        )
                }
        });
    }
}
