package org.motechproject.tama.ivr.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

public class CallStateCommand implements ITreeCommand {

    private CallState callState;
    private TAMAIVRContextFactory contextFactory;

    public CallStateCommand(CallState callState, TAMAIVRContextFactory contextFactory) {
        this.callState = callState;
        this.contextFactory = contextFactory;
    }

    public CallStateCommand(TAMAIVRContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = contextFactory.create((KooKooIVRContext) o);
        if (null == callState) {
            tamaivrContext.resetForMenuRepeat();
            tamaivrContext.doNoPromptForHangUp(true);
            tamaivrContext.callState(CallState.MAIN_MENU);
        } else {
            tamaivrContext.callState(callState);
        }
        return new String[0];
    }
}
