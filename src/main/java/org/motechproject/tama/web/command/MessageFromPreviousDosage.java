package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageFromPreviousDosage(IVRDayMessageBuilder ivrDayMessageBuilder) {
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return new String[0];
        }
        List<String> messages = new ArrayList<String>();

        messages.add(IVRMessage.YOUR);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(IVRMessage.DOSE_NOT_RECORDED);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
        messages.addAll(pillRegimenSnapshot.medicinesForPreviousDosage());
        messages.add(IVRMessage.FROM_THE_BOTTLE);
        messages.add(IVRMessage.PREVIOUS_DOSE_MENU);

        return messages.toArray(new String[0]);
    }
}
