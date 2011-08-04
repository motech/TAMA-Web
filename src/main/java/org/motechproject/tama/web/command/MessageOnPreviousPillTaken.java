package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPreviousPillTaken extends BaseTreeCommand {

    private PillReminderService service;

    @Autowired
    public MessageOnPreviousPillTaken(PillReminderService service) {
        this.service = service;
    }

    @Override
    public String[] execute(Object context) {

        IVRContext ivrContext = (IVRContext) context;
        DosageResponse previousDosage = service.getPreviousDosage(getRegimenIdFrom(ivrContext), getDosageIdFrom(ivrContext));
        IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder(getDosageIdFrom(ivrContext), previousDosage.getDosageId(), previousDosage.getDosageHour());

        ArrayList<String> messages = new ArrayList<String>();
        messages.add(IVRMessage.YOU_SAID_YOU_TOOK);
        messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAYS, IVRMessage.MORNING, IVRMessage.EVENING));
        messages.add(IVRMessage.DOSE);
        return messages.toArray(new String[]{});
    }
}