package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PillsDelayWarning extends DailyPillReminderTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;
    private TamaIVRMessage ivrMessage;

    @Autowired
    public PillsDelayWarning(TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (isLastReminder(context)) {
            DateTime nextDosageTime = pillRegimen(context).getNextDoseTime(context.callStartTime());
            List<String> messages = new ArrayList<String>();
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING);
            messages.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime, context.preferredLanguage()));
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING_PADDING);
            return messages.toArray(new String[messages.size()]);
        }
        return new String[]{
                TamaIVRMessage.PLEASE_TAKE_DOSE,
                ivrMessage.getNumberFilename(context.retryInterval()),
                TamaIVRMessage.CALL_AFTER_SOME_TIME
        };
    }

    private boolean isLastReminder(DailyPillReminderContext context) {
        int timesSent = context.numberOfTimesReminderSent();
        int totalTimesToSend = context.totalNumberOfTimesToSendReminder();
        return timesSent == totalTimesToSend;
    }
}