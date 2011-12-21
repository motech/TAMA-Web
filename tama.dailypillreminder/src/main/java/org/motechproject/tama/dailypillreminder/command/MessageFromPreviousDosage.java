package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends DailyPillReminderTreeCommand {
    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageFromPreviousDosage(PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(context);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return new String[0];
        }
        List<String> messages = new ArrayList<String>();

        messages.add(TamaIVRMessage.YOUR);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(pillRegimenSnapshot.getPreviousDoseTime()));
        messages.add(TamaIVRMessage.DOSE_NOT_RECORDED);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAY_IN_THE_MORNING(pillRegimenSnapshot.getPreviousDoseTime()));
        messages.add(TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
        messages.addAll(pillRegimenSnapshot.medicinesForPreviousDose());
        messages.add(TamaIVRMessage.FROM_THE_BOTTLE);
        messages.add(TamaIVRMessage.PREVIOUS_DOSE_MENU);

        return messages.toArray(new String[messages.size()]);
    }
}