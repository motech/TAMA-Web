package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillNotTaken extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public MessageOnPreviousPillNotTaken(IVRDayMessageBuilder ivrDayMessageBuilder, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(TamaIVRMessage.YOU_SAID_YOU_DID_NOT_TAKE);
        messages.addAll(ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(pillRegimenSnapshot(ivrContext).getPreviousDosageTime()));
        messages.add(TamaIVRMessage.DOSE);
        messages.add(TamaIVRMessage.TRY_NOT_TO_MISS);
        return messages.toArray(new String[messages.size()]);
    }
}