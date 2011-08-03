package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.web.command.PreviousPillTakenCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PreviousDosageReminderTree extends TAMADecisionTree {

    @Autowired
    private PreviousPillTakenCommand pillTakenCommand;

    @Override
    protected Node createRootNode() {
        return Node.newBuilder().setTransitions(new Object[][]{
                {"1", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setTreeCommands(pillTakenCommand)
                                        .setPrompts(Arrays.<Prompt>asList(
                                                new AudioPrompt().setName(IVRMessage.YOU_SAID_YOU_TOOK),
                                                new AudioPrompt().setName(IVRMessage.YESTERDAYS),
                                                new AudioPrompt().setName(IVRMessage.EVENING),
                                                new AudioPrompt().setName(IVRMessage.DOSE)))
                                        .build())
                        .build()
                },
                {"3", Transition.newBuilder()
                        .setDestinationNode(
                                Node.newBuilder()
                                        .setPrompts(Arrays.<Prompt>asList(
                                                new AudioPrompt().setName(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE),
                                                new AudioPrompt().setName(IVRMessage.YESTERDAYS),
                                                new AudioPrompt().setName(IVRMessage.EVENING),
                                                new AudioPrompt().setName(IVRMessage.DOSE),
                                                new AudioPrompt().setName(IVRMessage.TRY_NOT_TO_MISS)))
                                        .build())
                        .build()
                }
        }).build();
    }
}
