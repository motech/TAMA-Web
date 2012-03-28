package org.motechproject.tama.dailypillreminder.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.dailypillreminder.command.*;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATransitionFactory;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageReminderTree extends TamaDecisionTree {
    @Autowired
    private MessageOnPillTaken messageOnPillTaken;
    @Autowired
    private MessageForMedicines messageForMedicines;
    @Autowired
    private PillsDelayWarning pillsDelayWarning;
    @Autowired
    private RecordDeclinedDosageReasonCommand recordDeclinedDosageReasonCommand;
    @Autowired
    private UpdateAdherenceAsNotCapturedForCurrentDosageCommand updateAdherenceAsNotCapturedCommand;
    @Autowired
    private UpdateAdherenceAsCapturedForCurrentDosageCommand updateAdherenceAsCapturedCommand;
    @Autowired
    private AdherenceMessageWhenPreviousDosageCapturedCommand adherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private MissedPillFeedbackCommand forMissedPillFeedbackCommand;

    @Autowired
    public CurrentDosageReminderTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, this);
    }

    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(TamaIVRMessage.PILL_REMINDER_RESPONSE_MENU))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsCapturedCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new AudioPrompt().setCommand(adherenceWhenPreviousDosageCapturedCommand)
                                                ))
                        },
                        {"2", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsNotCapturedCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(pillsDelayWarning)
                                                ))
                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsCapturedCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(forMissedPillFeedbackCommand),
                                                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_CANNOT_BE_TAKEN_MENU))
                                                .setTransitions(new Object[][]{
                                                        {"1", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                                                        {"2", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setName(TamaIVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(adherenceWhenPreviousDosageCapturedCommand)
                                                                        ))
                                                        },
                                                        {"3", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setCommand(adherenceWhenPreviousDosageCapturedCommand)
                                                                        ))
                                                        }
                                                }))
                        }
                });
    }
}
