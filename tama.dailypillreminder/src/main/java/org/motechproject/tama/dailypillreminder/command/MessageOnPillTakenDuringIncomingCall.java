package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTakenDuringIncomingCall extends DailyPillReminderTreeCommand {
    private Integer dosageInterval;

    @Autowired
    public MessageOnPillTakenDuringIncomingCall(DailyPillReminderService dailyPillReminderService, @Value("#{dailyPillReminderProperties['"+ TAMAConstants.DOSAGE_INTERVAL  +"']}") Integer dosageInterval) {
        super(dailyPillReminderService);
        this.dosageInterval = dosageInterval;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();
        if (context.currentDose().isEarlyToTake(context.callStartTime(), dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_BEFORE_TIME);
        else if (context.currentDose().isLateToTake(context.callStartTime(), dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_LATE);
        else if (context.currentDose().isOnTime(context.callStartTime(), dosageInterval))
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);

        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[messages.size()]);
    }
}
