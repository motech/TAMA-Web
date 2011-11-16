package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageOnPillTakenDuringIncomingCall extends BaseTreeCommand {
    private TamaIVRMessage ivrMessage;

    @Autowired
    public MessageOnPillTakenDuringIncomingCall(TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        int dosageInterval = Integer.valueOf(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL));

        ArrayList<String> messages = new ArrayList<String>();
        PillRegimenSnapshot pillRegimenResponse = pillRegimenSnapshot(ivrContext);
        if (pillRegimenResponse.isEarlyToTakeDosage(dosageInterval))
            messages.add(TamaIVRMessage.TOOK_DOSE_BEFORE_TIME);
        else if (pillRegimenResponse.isLateToTakeDosage())
            messages.add(TamaIVRMessage.TOOK_DOSE_LATE);
        else if (pillRegimenResponse.hasTakenDosageOnTime(dosageInterval))
            messages.add(TamaIVRMessage.DOSE_TAKEN_ON_TIME);

        messages.add(TamaIVRMessage.DOSE_RECORDED);
        return messages.toArray(new String[messages.size()]);
    }
}
