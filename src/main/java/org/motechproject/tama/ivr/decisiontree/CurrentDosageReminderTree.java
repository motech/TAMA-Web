package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
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
    private MessageIfLastCall messageIfLastCall;
    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;
    @Autowired
    private RecordResponseInTamaCommand recordResponseInTamaCommand;
    @Autowired
    private PillTakenCommand pillTakenCommand;

    @Autowired
    private PreviousDosageTree previousDosageTree;

    protected Node createRootNode() {
        return Node.newBuilder()
                .setPrompts(Arrays.asList(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new AudioPrompt().setCommand(messageIfLastCall),
                        new AudioPrompt().setName(IVRMessage.PILL_REMINDER_RESPONSE_MENU)))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommand(pillTakenCommand)
                                                .setPrompts(Arrays.<Prompt>asList(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new AudioPrompt().setCommand(messageFromPreviousDosage))
                                                )
                                                .setTransitions(jumpToPreviousDosageTree())
                                                .build())
                                .build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(
                                                        new AudioPrompt().setName(IVRMessage.PLEASE_TAKE_DOSE),
                                                        new AudioPrompt().setName(IVRMessage.PILL_REMINDER_RETRY_INTERVAL),
                                                        new AudioPrompt().setName(IVRMessage.MINUTES)))
                                                .setTransitions(jumpToPreviousDosageTree())
                                                .build())
                                .build()
                        },
                        {"3", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(new AudioPrompt().setName(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU)))
                                                .setTransitions(new Object[][]{
                                                        {"2", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setPrompts(Arrays.asList(
                                                                                new AudioPrompt().setName(IVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(messageFromPreviousDosage))
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree())
                                                                        .build())
                                                                .build()
                                                        },
                                                        {"3", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setTreeCommand(recordResponseInTamaCommand)
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
        return previousDosageTree.getTree().getRootNode().getTransitions();
    }
}
