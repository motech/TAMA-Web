package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTaken extends BaseTreeCommand {
    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        ArrayList<String> messages = new ArrayList<String>();
        if (getTimesSent(ivrContext) == 0) {
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);
        }
        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[]{});
    }
}