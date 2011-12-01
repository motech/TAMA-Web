package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tamacallflow.ivr.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(new MenuAudioPrompt().setCommand(messageFromPreviousDosage))
                .setTransitions(new Object[][]{
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
