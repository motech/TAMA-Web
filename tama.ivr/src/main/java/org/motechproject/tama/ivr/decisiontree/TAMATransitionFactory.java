package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.command.CallStateCommand;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

public class TAMATransitionFactory {
    public static Transition createCallStateTransition(CallState callState) {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();
        return new Transition().setDestinationNode(
                        new Node()
                            .setTreeCommands(
                                new CallStateCommand(callState, contextFactory)
                            )
                    );
    }

    public static Transition createCallStateTransitionWithAudio(CallState callState, String audioFile) {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();
        return new Transition().setDestinationNode(
                        new Node()
                            .setPrompts(
                                new AudioPrompt().setName(audioFile)
                            )
                            .setTreeCommands(
                                    new CallStateCommand(callState, contextFactory)
                            )
                    );
    }


}
