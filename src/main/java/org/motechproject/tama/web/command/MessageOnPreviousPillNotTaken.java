package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillNotTaken extends BaseTreeCommand {
    @Override
    public String[] execute(Object context) {

        IVRContext ivrContext = (IVRContext) context;
        DosageResponse previousDosage = new PillRegimenSnapshot(ivrContext).getPreviousDosage();
        IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder(getDosageIdFrom(ivrContext), previousDosage.getDosageId(), previousDosage.getDosageHour());

        ArrayList<String> messages = new ArrayList<String>();
        messages.add(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE);
        messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAYS, IVRMessage.MORNING, IVRMessage.EVENING));
        messages.add(IVRMessage.DOSE);
        messages.add(IVRMessage.TRY_NOT_TO_MISS);
        return messages.toArray(new String[]{});
    }
}