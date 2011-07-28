package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.web.command.FirstTimeReminderCommand;
import org.motechproject.tama.web.command.JumpToSymptomsModuleCommand;
import org.motechproject.tama.web.command.RecordResponseInTamaCommand;
import org.motechproject.tama.web.command.ScheduleCallCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PillReminderTree {
    @Autowired
    private FirstTimeReminderCommand firstTimeReminderCommand;
    @Autowired
    private ScheduleCallCommand scheduleCallCommand;
    @Autowired
    private RecordResponseInTamaCommand recordResponseInTamaCommand;
    @Autowired
    private JumpToSymptomsModuleCommand jumpToSymptomsModuleCommand;

    public static final String AUDIO_RECORD_CURRENT_DOSAGE = "AUDIO_RECORD_CURRENT_DOSAGE";
    public static final String AUDIO_PILL_TAKEN_ON_TIME = "AUDIO_PILL_TAKEN_ON_TIME";
    public static final String AUDIO_GETTING_LATE_FOR_PILL = "AUDIO_GETTING_LATE_FOR_PILL";
    public static final String AUDIO_RECORD_REASON_FOR_NOT_TAKING_PILL = "AUDIO_RECORD_REASON_FOR_NOT_TAKING_PILL";
    public static final String AUDIO_CARRY_EXTRA_PILLS = "AUDIO_CARRY_EXTRA_PILLS";
    private Tree tree;

    public Tree createTree() {
        if (tree != null) return tree;

        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(AudioPrompt.newBuilder().setName(AUDIO_RECORD_CURRENT_DOSAGE).build()))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommand(firstTimeReminderCommand)
                                                .setTransitions(new Object[][]{
                                                        {"Yes", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setPrompts(Arrays.asList(AudioPrompt.newBuilder().setName(AUDIO_PILL_TAKEN_ON_TIME).build()))
                                                                        .setTreeCommand(recordResponseInTamaCommand)
                                                                        .build())
                                                                .build()
                                                        },
                                                        {"No", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setTreeCommand(recordResponseInTamaCommand)
                                                                        .build())
                                                                .build()
                                                        }
                                                })
                                                .build())
                                .build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(AudioPrompt.newBuilder().setName(AUDIO_GETTING_LATE_FOR_PILL).build()))
                                                .setTreeCommand(scheduleCallCommand)
                                                .build())
                                .build()
                        },
                        {"3", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setPrompts(Arrays.<Prompt>asList(AudioPrompt.newBuilder().setName(AUDIO_RECORD_REASON_FOR_NOT_TAKING_PILL).build()))
                                                .setTransitions(new Object[][]{
                                                        {"1", Transition.newBuilder()
                                                                .setDestinationNode(Node.newBuilder()
                                                                        .setPrompts(Arrays.asList(AudioPrompt.newBuilder().setName(AUDIO_CARRY_EXTRA_PILLS).build()))
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
