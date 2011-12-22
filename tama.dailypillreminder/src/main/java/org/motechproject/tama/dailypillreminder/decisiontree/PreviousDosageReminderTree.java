package org.motechproject.tama.dailypillreminder.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.dailypillreminder.command.*;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PreviousDosageReminderTree extends TamaDecisionTree {
    @Autowired
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;
    @Autowired
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;
    @Qualifier("adherenceMessageCommand")
    @Autowired
    private AdherenceMessageCommand adherenceMessageCommand;
    @Autowired
    private UpdateAdherenceAsCapturedForPreviousDosageCommand updateAdherenceAsCapturedForPreviousDosageCommand;
    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;

    @Autowired
    public PreviousDosageReminderTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER, this);
    }

    @Override
    protected Node createRootNode() {
        return new Node()
                .setPrompts(new MenuAudioPrompt().setCommand(messageFromPreviousDosage))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsCapturedForPreviousDosageCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPreviousPillTaken),
                                                        new AudioPrompt().setCommand(adherenceMessageCommand)
                                                )
                                )

                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsCapturedForPreviousDosageCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPreviousPillNotTaken),
                                                        new AudioPrompt().setCommand(adherenceMessageCommand)
                                                )
                                )
                        }
                });
    }
}
