package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;

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
