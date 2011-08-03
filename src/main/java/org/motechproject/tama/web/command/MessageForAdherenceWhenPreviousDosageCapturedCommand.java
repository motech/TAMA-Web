package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends BaseTreeCommand {

    @Autowired
    private PillReminderService pillReminderService;

    @Autowired
    private DosageAdherenceLogs dosageAdherenceLogs;


    public MessageForAdherenceWhenPreviousDosageCapturedCommand() {
    }

    public MessageForAdherenceWhenPreviousDosageCapturedCommand(PillReminderService pillReminderService, DosageAdherenceLogs dosageAdherenceLogs) {
        this.pillReminderService = pillReminderService;
        this.dosageAdherenceLogs = dosageAdherenceLogs;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String dosageId = getDosageIdFrom(ivrContext);
        DosageResponse dosageResponse = pillReminderService.getPreviousDosage(getRegimenIdFrom(ivrContext), dosageId);
        String previousDosageId = dosageResponse.getDosageId();
        if (isPreviousDosagePresent(previousDosageId) || dosageAdherenceLogs.isPreviousDosageTaken(previousDosageId)) {
            return new String[]{String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, getAdherencePercentage(dosageId))};
        }

        return new String[0];
    }

    private int getAdherencePercentage(String dosageId) {
        LocalDate fromDate = DateUtility.today();
        LocalDate toDate = DateUtility.addDaysToLocalDate(fromDate, -28);
        int scheduledDosagesTotalCount = dosageAdherenceLogs.findScheduledDosagesTotalCount(dosageId, fromDate, toDate);
        int scheduledDosagesSuccessCount = dosageAdherenceLogs.findScheduledDosagesSuccessCount(dosageId, fromDate, toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }

    private boolean isPreviousDosagePresent(String previousDosageId) {
        return previousDosageId == null;
    }
}