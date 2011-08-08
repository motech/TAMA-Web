package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillTaken extends BaseTreeCommand {
    @Override
    public String[] execute(Object context) {

        IVRContext ivrContext = (IVRContext) context;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        ArrayList<String> messages = new ArrayList<String>();
        messages.add(IVRMessage.YOU_SAID_YOU_TOOK);
        messages.addAll(new IVRDayMessageBuilder().getMessageForPreviousDosage_YESTERDAYS_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(IVRMessage.DOSE);
        return messages.toArray(new String[]{});
    }
}