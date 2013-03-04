package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends DailyPillReminderTreeCommand {
    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageFromPreviousDosage(DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (context.isPreviousDoseTaken()) {
            return new String[0];
        }
        List<String> messages = new ArrayList<String>();

        messages.add(TamaIVRMessage.YOUR);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(context.previousDose().getDoseTime()));
        messages.add(TamaIVRMessage.DOSE_NOT_RECORDED);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageQuestion_YESTERDAY_IN_THE_MORNING(context.previousDose().getDoseTime()));
        messages.add(TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
        messages.add(TamaIVRMessage.FROM_THE_BOTTLE_FOR_PREVIOUS_DOSAGE);
        messages.add(TamaIVRMessage.PREVIOUS_DOSE_MENU);

        return messages.toArray(new String[messages.size()]);
    }
}
