package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PillsDelayWarning extends DailyPillReminderTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;
    private Integer pillReminderLag;


    @Autowired
    public PillsDelayWarning(DailyPillReminderService dailyPillReminderService,
                             @Value("#{dailyPillReminderProperties['reminder.lag.mins']}") Integer pillReminderLag) {
        super(dailyPillReminderService);
        this.pillReminderLag = pillReminderLag;
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (isLastReminder(context)) {
            List<String> messages = new ArrayList<String>();
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING);
            messages.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextCallTime(context), context.preferredLanguage()));
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING_PADDING);
            return messages.toArray(new String[messages.size()]);
        }
        return new String[]{
                TamaIVRMessage.PLEASE_TAKE_DOSE,
                TamaIVRMessage.getNumberFilename(context.retryInterval()),
                TamaIVRMessage.CALL_AFTER_SOME_TIME
        };
    }

    private DateTime nextCallTime(DailyPillReminderContext context) {
        return context.nextDose().getDoseTime().plusMinutes(pillReminderLag);
    }

    private boolean isLastReminder(DailyPillReminderContext context) {
        int timesSent = context.numberOfTimesReminderSent();
        int totalTimesToSend = context.totalNumberOfTimesToSendReminder();
        return timesSent == totalTimesToSend;
    }
}