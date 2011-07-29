package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.ivr.IVRMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageOnPillTaken implements ITreeCommand {
    @Override
    public String[] execute(Object obj) {
        return new String[]{IVRMessage.DOSE_TAKEN};
    }
}