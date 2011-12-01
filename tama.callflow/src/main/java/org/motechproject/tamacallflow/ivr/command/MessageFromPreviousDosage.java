package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends BaseTreeCommand {
    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageFromPreviousDosage(IVRDayMessageBuilder ivrDayMessageBuilder, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(ivrContext);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return new String[0];
        }
        List<String> messages = new ArrayList<String>();

        messages.add(TamaIVRMessage.YOUR);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(TamaIVRMessage.DOSE_NOT_RECORDED);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(pillRegimenSnapshot.getPreviousDosageTime()));
        messages.add(TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
        messages.addAll(pillRegimenSnapshot.medicinesForPreviousDosage());
        messages.add(TamaIVRMessage.FROM_THE_BOTTLE);
        messages.add(TamaIVRMessage.PREVIOUS_DOSE_MENU);

        return messages.toArray(new String[messages.size()]);
    }
}
