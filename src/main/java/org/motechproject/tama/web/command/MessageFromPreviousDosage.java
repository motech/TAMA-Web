package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends BaseTreeCommand {
    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DosageResponse previousDosage = pillRegimenSnapshot.getPreviousDosage();

        List<String> messages = new ArrayList<String>();
        if ("previousDosageId".equals(previousDosage.getDosageId())) {
            IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder(getDosageIdFrom(ivrContext), previousDosage.getDosageId(), previousDosage.getDosageHour());
            messages.add(IVRMessage.YOUR);
            messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAYS, IVRMessage.MORNING, IVRMessage.EVENING));
            messages.add(IVRMessage.DOSE_NOT_RECORDED);
            messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAY, IVRMessage.IN_THE_MORNING, IVRMessage.IN_THE_EVENING));
            messages.add(IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
            messages.addAll(pillRegimenSnapshot.medicinesForPreviousDosage());
            messages.add(IVRMessage.FROM_THE_BOTTLE);
            messages.add(IVRMessage.PREVIOUS_DOSE_MENU);
        }
        return messages.toArray(new String[]{});
    }
}
