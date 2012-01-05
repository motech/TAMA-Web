package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTakenDuringIncomingCall extends DailyPillReminderTreeCommand {
    private Integer dosageInterval;

    @Autowired
    public MessageOnPillTakenDuringIncomingCall(PillReminderService pillReminderService, @Value("#{dailyPillReminderProperties['"+ TAMAConstants.DOSAGE_INTERVAL  +"']}") Integer dosageInterval) {
        super(pillReminderService);
        this.dosageInterval = dosageInterval;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();
        PillRegimenSnapshot pillRegimenResponse = pillRegimenSnapshot(context);
        if (pillRegimenResponse.isEarlyToTakeDose(dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_BEFORE_TIME);
        else if (pillRegimenResponse.isLateToTakeDose(dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_LATE);
        else if (pillRegimenResponse.hasTakenDosageOnTime(dosageInterval))
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);

        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[messages.size()]);
    }
}
