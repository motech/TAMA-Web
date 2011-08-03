package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.web.command.MessageOnPreviousPillNotTaken;
import org.motechproject.tama.web.command.MessageOnPreviousPillTaken;
import org.motechproject.tama.web.command.PreviousPillTakenCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PreviousDosageReminderTree extends TAMADecisionTree {

    @Autowired
    private PreviousPillTakenCommand pillTakenCommand;
    @Autowired
    private MessageOnPreviousPillTaken messageOnPreviousPillTaken;
    @Autowired
    private MessageOnPreviousPillNotTaken messageOnPreviousPillNotTaken;

    @Override
    protected Node createRootNode() {
        return Node.newBuilder().setTransitions(new Object[][]{
                {"1", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setTreeCommands(pillTakenCommand, messageOnPreviousPillTaken)
                                        .build())
                        .build()
                },
                {"3", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setTreeCommands(messageOnPreviousPillNotTaken)
                                        .build())
                        .build()
                }
        }).build();
    }
}
