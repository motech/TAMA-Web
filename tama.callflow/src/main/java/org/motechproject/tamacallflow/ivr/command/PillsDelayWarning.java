package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PillsDelayWarning extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;
    private TamaIVRMessage ivrMessage;

    @Autowired
    public PillsDelayWarning(TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        if (isLastReminder(ivrContext)) {
            DateTime nextDosageTime = pillRegimenSnapshot(ivrContext).getNextDoseTime();
            List<String> messages = new ArrayList<String>();
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING);
            messages.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime, ivrContext.preferredLanguage()));
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING_PADDING);
            return messages.toArray(new String[messages.size()]);
        }
        return new String[]{
                TamaIVRMessage.PLEASE_TAKE_DOSE,
                ivrMessage.getNumberFilename(ivrContext.retryInterval()),
                TamaIVRMessage.CALL_AFTER_SOME_TIME
        };
    }

    private boolean isLastReminder(TAMAIVRContext ivrContext) {
        int timesSent = ivrContext.numberOfTimesReminderSent();
        int totalTimesToSend = ivrContext.totalNumberOfTimesToSendReminder();
        return timesSent == totalTimesToSend;
    }
}