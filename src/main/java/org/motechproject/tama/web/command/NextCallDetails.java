package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NextCallDetails extends BaseTreeCommand {

    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;

        DateTime nextDosageTime = new PillRegimenSnapshot(ivrContext).getNextDosageTime();
        List<String> messageForNextDosage = new ArrayList<String>();
        messageForNextDosage.add(IVRMessage.YOUR_NEXT_DOSE_IS);
        messageForNextDosage.add(IVRMessage.AT);
        messageForNextDosage.addAll(new IVRDayMessageBuilder().getMessageForNextDosage(nextDosageTime));

        return  messageForNextDosage.toArray(new String[messageForNextDosage.size()]);
    }
}
