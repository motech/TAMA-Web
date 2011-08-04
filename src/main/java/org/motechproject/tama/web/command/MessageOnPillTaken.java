package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageOnPillTaken extends BaseTreeCommand {
    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        int timesSent = getTimesSent(ivrContext);
        return timesSent == 0 ? new String[]{ IVRMessage.DOSE_TAKEN } : new String[0];
    }
}