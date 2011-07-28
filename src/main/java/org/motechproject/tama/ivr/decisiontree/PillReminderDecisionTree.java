package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PillReminderDecisionTree {
    @Autowired
    private MessageOnPillTakenCommand messageOnPillTakenCommand;
    @Autowired
    private ScheduleCallCommand scheduleCallCommand;
    @Autowired
    private RecordResponseInTamaCommand recordResponseInTamaCommand;
    @Autowired
    private PillTakenCommand pillTakenCommand;

    private Tree tree;

    public Tree createTree() {
        if (tree != null) return tree;

        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(AudioPrompt.newBuilder().setName(IVRMessage.PILL_REMINDER_RESPONSE_MENU).build()))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommand(pillTakenCommand)
                                                .setPrompts(Arrays.<Prompt>asList(AudioPrompt.newBuilder().setAudioCommand(messageOnPillTakenCommand).build()))
                                                .build())
                                .build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(AudioPrompt.newBuilder().setName(IVRMessage.PLEASE_TAKE_DOSE).build(),
                                                        AudioPrompt.newBuilder().setName(IVRMessage.PILL_REMINDER_RETRY_INTERVAL).build(),
                                                        AudioPrompt.newBuilder().setName(IVRMessage.MINUTES).build()))
                                                .setTreeCommand(scheduleCallCommand)
                                                .build())
                                .build()
                        },
                        {"3", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(AudioPrompt.newBuilder().setName(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU).build()))
                                                .setTransitions(new Object[][]{
                                                        {"1", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setPrompts(Arrays.asList(AudioPrompt.newBuilder().setName(IVRMessage.PLEASE_CARRY_SMALL_BOX).build()))
                                                                        .build())
                                                                .build()
                                                        },
                                                        {"2", Transition.newBuilder()
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

        tree = Tree.newBuilder()
                .setName("PillReminderTree")
                .setRootNode(rootNode)
                .build();
        return tree;
    }

    public Tree getTree() {
        createTree();
        return tree;
    }
}
