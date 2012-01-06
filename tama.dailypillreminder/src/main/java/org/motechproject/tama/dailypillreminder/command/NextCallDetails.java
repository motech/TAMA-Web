package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.builder.IVRDayMessageBuilder;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NextCallDetails extends DailyPillReminderTreeCommand {
    private IVRDayMessageBuilder ivrDayMessageBuilder;

    @Autowired
    public NextCallDetails(PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (context.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN)) {
            return new String[0];
        }

        DateTime nextDosageTime = pillRegimen(context).getNextDoseTime(context.callStartTime());
        List<String> messageForNextDosage = new ArrayList<String>();
        messageForNextDosage.add(TamaIVRMessage.YOUR_NEXT_DOSE_IS);
        messageForNextDosage.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime, context.preferredLanguage()));
        messageForNextDosage.add(TamaIVRMessage.YOUR_NEXT_DOSE_IS_PADDING);

        return messageForNextDosage.toArray(new String[messageForNextDosage.size()]);
    }
}
