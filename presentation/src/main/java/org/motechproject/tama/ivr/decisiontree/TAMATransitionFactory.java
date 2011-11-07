package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.web.command.CallStateCommand;

public class TAMATransitionFactory {
    public static Transition createCallStateTransition(CallState callState) {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();
        return new Transition().setDestinationNode(new Node().setTreeCommands(new CallStateCommand(callState, contextFactory)));
    }
}
