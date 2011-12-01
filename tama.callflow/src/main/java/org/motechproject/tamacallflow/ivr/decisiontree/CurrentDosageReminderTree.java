package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.command.*;
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
    private StopTodaysRemindersCommand stopTodaysRemindersCommand;
    @Autowired
    private UpdateAdherenceCommand updateAdherenceCommand;
    @Autowired
    private MessageForAdherenceWhenPreviousDosageCapturedCommand messageForAdherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private MessageForMissedPillFeedbackCommand messageForMissedPillFeedbackCommand;

    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(TamaIVRMessage.PILL_REMINDER_RESPONSE_MENU))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                //TODO: stopTodaysRemindersCommand, updateAdherenceCommand should be combined to form a DosageTakenCommand
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand)
                                                ))
                        },
                        {"2", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(pillsDelayWarning)
                                                ))
                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageForMissedPillFeedbackCommand),
                                                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_CANNOT_BE_TAKEN_MENU))
                                                .setTransitions(new Object[][]{
                                                        {"1", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                                                        {"2", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setName(TamaIVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand)
                                                                        ))
                                                        },
                                                        {"3", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand)
                                                                        ))
                                                        }
                                                }))
                        }
                });
    }
}
