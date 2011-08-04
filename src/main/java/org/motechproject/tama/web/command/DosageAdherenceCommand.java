package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DosageAdherenceCommand extends BaseTreeCommand {
    @Autowired
    protected DosageAdherenceLogs dosageAdherenceLogs;

    protected DosageAdherenceCommand() {
    }

    public DosageAdherenceCommand(DosageAdherenceLogs dosageAdherenceLogs) {
        this.dosageAdherenceLogs = dosageAdherenceLogs;
    }

    protected int getAdherencePercentage(String regimenId, LocalDate toDate, int scheduledDosagesTotalCount) {
        int scheduledDosagesSuccessCount = dosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, toDate.minusDays(TAMAConstants.DAYS_IN_FOUR_WEEKS), toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }
}