package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DosageAdherenceCommand extends BaseTreeCommand {
    @Autowired
    protected DosageAdherenceLogs dosageAdherenceLogs;

    protected DosageAdherenceCommand() {
    }

    public DosageAdherenceCommand(DosageAdherenceLogs dosageAdherenceLogs) {
        this.dosageAdherenceLogs = dosageAdherenceLogs;
    }

    protected int getAdherencePercentage(String regimenId) {
        LocalDate fromDate = DateUtility.today();
        LocalDate toDate = DateUtility.addDaysToLocalDate(fromDate, -28);
        int scheduledDosagesTotalCount = dosageAdherenceLogs.findScheduledDosagesTotalCount(regimenId, fromDate, toDate);
        int scheduledDosagesSuccessCount = dosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, fromDate, toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }
}