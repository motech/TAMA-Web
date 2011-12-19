package org.motechproject.tama.ivr.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public abstract class BaseTreeCommand implements ITreeCommand {
    @Override
    public String[] execute(Object o) {
        KooKooIVRContext ivrContext = (KooKooIVRContext) o;
        return executeCommand(new TAMAIVRContext(ivrContext));
    }

    public abstract String[] executeCommand(TAMAIVRContext tamaivrContext);
}