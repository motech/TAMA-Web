package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillNotTaken extends DailyPillReminderTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageOnPreviousPillNotTaken(DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(TamaIVRMessage.YOU_SAID_YOU_DID_NOT_TAKE);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageConfirmation_YESTERDAYS_MORNING(context.previousDose().getDoseTime()));
        messages.add(TamaIVRMessage.DOSE_NOT_TAKEN);
        messages.add(TamaIVRMessage.DOSE_RECORDED);
        messages.add(TamaIVRMessage.TRY_NOT_TO_MISS);
        return messages.toArray(new String[messages.size()]);
    }
}