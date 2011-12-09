package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillTaken extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageOnPreviousPillTaken(IVRDayMessageBuilder ivrDayMessageBuilder, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(TamaIVRMessage.YOU_SAID_YOU_TOOK);
        messages.add(ivrDayMessageBuilder.getMessageForPreviousDosageConfirmation_YESTERDAYS_MORNING(pillRegimenSnapshot(ivrContext).getPreviousDosageTime()));
        messages.add(TamaIVRMessage.DOSE_TAKEN);
        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[messages.size()]);
    }
}