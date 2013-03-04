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
        if (pillRegimen.isWithinPillWindow(context.callStartTime())) {
            messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW);
            messages.add(TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW);
        } else {
            messages.add(TamaIVRMessage.NOT_REPORTED_IF_TAKEN);
            messages.add(TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_AFTER_PILL_WINDOW);
        }

        return messages.toArray(new String[messages.size()]);
    }

}
