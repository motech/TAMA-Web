package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends DosageAdherenceCommand {

    @Autowired
    private PillReminderService pillReminderService;

    public MessageForAdherenceWhenPreviousDosageCapturedCommand() {
    }

    @Autowired
    public MessageForAdherenceWhenPreviousDosageCapturedCommand(PillReminderService pillReminderService, DosageAdherenceLogs dosageAdherenceLogs) {
        super(dosageAdherenceLogs);
        this.pillReminderService = pillReminderService;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        DosageResponse dosageResponse = pillReminderService.getPreviousDosage(regimenId, getDosageIdFrom(ivrContext));
        String previousDosageId = dosageResponse.getDosageId();
        if (isFirstDosage(previousDosageId) || dosageAdherenceLogs.isPreviousDosageTaken(previousDosageId)) {
            return new String[]{String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, getAdherencePercentage(regimenId))};
        }

        return new String[0];
    }

    private boolean isFirstDosage(String previousDosageId) {
        return previousDosageId == null;
    }
}