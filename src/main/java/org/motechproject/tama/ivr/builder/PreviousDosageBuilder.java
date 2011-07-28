package org.motechproject.tama.ivr.builder;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.call.PillReminderCall;

public class PreviousDosageBuilder {

    private IVRRequest ivrRequest;
    private PillReminderService service;
    private IVRMessage messages;

    public PreviousDosageBuilder(IVRRequest ivrRequest, PillReminderService service, IVRMessage messages) {
        this.ivrRequest = ivrRequest;
        this.service = service;
        this.messages = messages;
    }

    public IVRResponseBuilder build(IVRResponseBuilder ivrResponseBuilder) {
        String regimenId = (String) ivrRequest.getTamaParams().get(PillReminderCall.REGIMEN_ID);
        String dosageId = (String) ivrRequest.getTamaParams().get(PillReminderCall.DOSAGE_ID);
        String previousDosageId = service.getPreviousDosage(regimenId, dosageId);
        if (previousDosageId == "hasnotbeentaken") {
            return ivrResponseBuilder
                    .collectDtmf()
                    .withPlayAudios(
                        IVRMessage.YOUR,
                        IVRMessage.YESTERDAYS,
                        IVRMessage.EVENING,
                        IVRMessage.DOSE_NOT_RECORDED,
                        IVRMessage.YESTERDAY,
                        IVRMessage.IN_THE_EVENING,
                        IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE,
                        IVRMessage.FROM_THE_BOTTLE,
                        IVRMessage.PREVIOUS_DOSE_MENU
                    );
        }
        return ivrResponseBuilder.withHangUp();
    }
}
