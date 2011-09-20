package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillNotTaken extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageOnPreviousPillNotTaken(IVRDayMessageBuilder ivrDayMessageBuilder) {
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] execute(Object context) {

        IVRContext ivrContext = (IVRContext) context;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        ArrayList<String> messages = new ArrayList<String>();
        messages.add(TamaIVRMessage.YOU_SAID_YOU_DID_NOT_TAKE);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(TamaIVRMessage.DOSE);
        messages.add(TamaIVRMessage.TRY_NOT_TO_MISS);
        return messages.toArray(new String[]{});
    }
}