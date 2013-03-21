package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageForMedicinesDuringIncomingCall extends DailyPillReminderTreeCommand {

    @Autowired
    public MessageForMedicinesDuringIncomingCall(DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();

        PillRegimen pillRegimen = context.pillRegimen();
        if (!pillRegimen.isWithinPillWindow(context.callStartTime())) {
            messages.add(TamaIVRMessage.NOT_REPORTED_IF_TAKEN);
        }

        return messages.toArray(new String[messages.size()]);
    }

}
