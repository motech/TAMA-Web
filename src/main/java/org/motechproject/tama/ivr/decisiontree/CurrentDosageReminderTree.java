package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.*;
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
    private MessageFromPreviousDosage messageFromPreviousDosage;
    @Autowired
    private MessageForAdherenceWhenPreviousDosageCapturedCommand messageForAdherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private MessageForMissedPillFeedbackCommand messageForMissedPillFeedbackCommand;
    @Autowired
    private Regimen1To6Tree regimen1To6Tree;
    
    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(TamaIVRMessage.PILL_REMINDER_RESPONSE_MENU))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                ))
                        },
                        {"2", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(pillsDelayWarning),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage)))
                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageForMissedPillFeedbackCommand),
                                                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_CANNOT_BE_TAKEN_MENU))
                                                .setTransitions(new Object[][]{
                                                		{"1", new Transition().setDestinationNode(regimen1To6Tree.getTree().getRootNode())},
                                                        {"2", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setName(TamaIVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        ))
                                                        },
                                                        {"3", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        ))
                                                        }
                                                }))
                        }
                });
    }
}
