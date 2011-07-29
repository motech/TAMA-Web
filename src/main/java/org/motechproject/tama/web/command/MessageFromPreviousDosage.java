package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageFromPreviousDosage implements ITreeCommand {
    private PillReminderService service;

    @Autowired
    public MessageFromPreviousDosage(PillReminderService service) {
        this.service = service;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = (String) ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.REGIMEN_ID);
        String dosageId = (String) ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.DOSAGE_ID);
        String previousDosageId = service.getPreviousDosage(regimenId, dosageId);
        if ("hasnotbeentaken".equals(previousDosageId)) {
            return new String[] {
                            IVRMessage.YOUR,
                            IVRMessage.YESTERDAYS,
                            IVRMessage.EVENING,
                            IVRMessage.DOSE_NOT_RECORDED,
                            IVRMessage.YESTERDAY,
                            IVRMessage.IN_THE_EVENING,
                            IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE,
                            IVRMessage.FROM_THE_BOTTLE,
                            IVRMessage.PREVIOUS_DOSE_MENU
            };
        }
        return new String[0];
    }
}
