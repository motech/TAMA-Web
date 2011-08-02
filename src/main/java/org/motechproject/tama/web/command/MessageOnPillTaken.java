package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageOnPillTaken implements ITreeCommand {
    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        Map params = ivrContext.ivrRequest().getTamaParams();
        int timesSent = Integer.parseInt(String.valueOf(params.get(PillReminderCall.TIMES_SENT)));
        return new String[]{ timesSent == 0? IVRMessage.DOSE_TAKEN : "" };
    }
}