package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PillsDelayWarning extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    private TamaIVRMessage ivrMessage;

    @Autowired
    public PillsDelayWarning(IVRDayMessageBuilder ivrDayMessageBuilder, TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        if (isLastReminder(ivrContext)) {
            DateTime nextDosageTime = pillRegimenSnapshot(ivrContext).getNextDosageTime();
            List<String> messages = new ArrayList<String>();
            messages.add(TamaIVRMessage.LAST_REMINDER_WARNING);
            messages.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime));
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