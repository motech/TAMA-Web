package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Scope("session")
public class PreviousDosageReminderTree extends TAMADecisionTree {
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
        return Node.newBuilder().setTransitions(new Object[][]{
                {"1", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setTreeCommands(stopPreviousPillReminderCommand, updatePreviousPillAdherenceCommand)
                                        .setPrompts(Arrays.<Prompt>asList(
                                                new AudioPrompt().setCommand(messageOnPreviousPillTaken),
                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageNotCapturedCommand)))
                                        .build())
                        .build()
                },
                {"3", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setTreeCommands(stopPreviousPillReminderCommand, updatePreviousPillAdherenceCommand)
                                        .setPrompts(Arrays.<Prompt>asList(
                                                new AudioPrompt().setCommand(messageOnPreviousPillNotTaken),
                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageNotCapturedCommand)))
                                        .build())
                        .build()
                }
        }).build();
    }
}
