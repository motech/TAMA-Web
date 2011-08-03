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
        return new String[]{ timesSent == 0? IVRMessage.DOSE_TAKEN : "" };
    }
}