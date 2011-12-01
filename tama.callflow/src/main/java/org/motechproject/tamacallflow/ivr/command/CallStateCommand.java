package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;

public class CallStateCommand implements ITreeCommand {
    private CallState callState;
    private TAMAIVRContextFactory contextFactory;

    public CallStateCommand(CallState callState, TAMAIVRContextFactory contextFactory) {
        this.callState = callState;
        this.contextFactory = contextFactory;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = contextFactory.create((KooKooIVRContext) o);
        tamaivrContext.callState(callState);
        return new String[0];
    }
}
