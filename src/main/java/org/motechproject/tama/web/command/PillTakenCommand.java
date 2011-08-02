package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PillTakenCommand implements ITreeCommand {
    private PillReminderService pillReminderService;

    @Autowired
    public PillTakenCommand(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;

        Map tamaParams = ivrContext.ivrRequest().getTamaParams();
        String regimenId = (String) tamaParams.get(PillReminderCall.REGIMEN_ID);
        String dosageId = (String) tamaParams.get(PillReminderCall.DOSAGE_ID);
        pillReminderService.updateDosageTaken(regimenId, dosageId);
        return new String[0];
    }
}
