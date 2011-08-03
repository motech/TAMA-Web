package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class CurrentDosageReminderTree extends TAMADecisionTree {
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
    private PreviousDosageReminderTree previousDosageReminderTree;
    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;
    @Autowired
    private MessageForAdherenceWhenPreviousDosageCapturedCommand messageForAdherenceWhenPreviousDosageCapturedCommand;

    protected Node createRootNode() {
        return Node.newBuilder()
                .setPrompts(Arrays.asList(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new AudioPrompt().setCommand(pillsDelayWarning),
                        new MenuAudioPrompt().setName(IVRMessage.PILL_REMINDER_RESPONSE_MENU)))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(Arrays.<Prompt>asList(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage),
                                                        new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand))
                                                )
                                                .setTransitions(jumpToPreviousDosageTree())
                                                .build())
                                .build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommands(updateAdherenceCommand)
                                                .setPrompts(Arrays.<Prompt>asList(
                                                        new AudioPrompt().setCommand(pillsDelayWarning),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage)))
                                                .setTransitions(jumpToPreviousDosageTree())
                                                .build())
                                .build()
                        },
                        {"3", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(Arrays.<Prompt>asList(new MenuAudioPrompt().setName(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU)))
                                                .setTransitions(new Object[][]{
                                                        {"2", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(Arrays.asList(
                                                                                new AudioPrompt().setName(IVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage))
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree())
                                                                        .build())
                                                                .build()
                                                        },
                                                        {"3", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .build())
                                                                .build()
                                                        }
                                                })
                                                .build())
                                .build()
                        }
                })
                .build();
    }

    private Map<String, Transition> jumpToPreviousDosageTree() {
        return previousDosageReminderTree.getTree().getRootNode().getTransitions();
    }

}
