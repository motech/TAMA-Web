package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends BasePreviousDosageCommand {
    private PillReminderService service;

    @Autowired
    public MessageFromPreviousDosage(PillReminderService service) {
        this.service = service;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        DosageResponse previousDosage = service.getPreviousDosage(getRegimenIdFrom(ivrContext), getDosageIdFrom(ivrContext));

        List<String> messages = new ArrayList<String>();
        if ("previousDosageId".equals(previousDosage.getDosageId())) {
            messages.add(IVRMessage.YOUR);
            messages.add(IVRMessage.YESTERDAYS);
            messages.add(IVRMessage.EVENING);
            messages.add(IVRMessage.DOSE_NOT_RECORDED);
            messages.add(IVRMessage.YESTERDAY);
            messages.add(IVRMessage.IN_THE_EVENING);
            messages.add(IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
            messages.addAll(previousDosage.getMedicines());
            messages.add(IVRMessage.FROM_THE_BOTTLE);
            messages.add(IVRMessage.PREVIOUS_DOSE_MENU);
        }
        return messages.toArray(new String[]{});
    }

}
