package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.call.PillReminderCall;

public abstract class BaseTreeCommand implements ITreeCommand {

    protected int getTimesSent(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TIMES_SENT).toString());
    }

    protected int getTotalTimesToSend(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TOTAL_TIMES_TO_SEND).toString());
    }
}