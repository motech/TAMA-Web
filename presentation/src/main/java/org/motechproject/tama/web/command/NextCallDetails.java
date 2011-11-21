package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NextCallDetails extends BaseTreeCommand {
    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public NextCallDetails(IVRDayMessageBuilder ivrDayMessageBuilder, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        if (ivrContext.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN)) {
            return new String[0];
        }

        DateTime nextDosageTime = pillRegimenSnapshot(ivrContext).getNextDosageTime();
        List<String> messageForNextDosage = new ArrayList<String>();
        messageForNextDosage.add(TamaIVRMessage.YOUR_NEXT_DOSE_IS);
        messageForNextDosage.add(TamaIVRMessage.AT);
        messageForNextDosage.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime));
        messageForNextDosage.add(TamaIVRMessage.YOUR_NEXT_DOSE_IS_PADDING);

        return messageForNextDosage.toArray(new String[messageForNextDosage.size()]);
    }
}
