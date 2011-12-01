package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.command.CallStateCommand;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;

public class TAMATransitionFactory {
    public static Transition createCallStateTransition(CallState callState) {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();
        return new Transition().setDestinationNode(new Node().setTreeCommands(new CallStateCommand(callState, contextFactory)));
    }
}
