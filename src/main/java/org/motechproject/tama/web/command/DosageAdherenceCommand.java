package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
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

    protected int getAdherencePercentage(String regimenId, DateTime toDate, int scheduledDosagesTotalCount) {
        int scheduledDosagesSuccessCount = dosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, toDate.minusDays(TAMAConstants.DAYS_IN_FOUR_WEEKS).toLocalDate(), toDate.toLocalDate());
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }

    protected String[] getAdherenceMessage(String regimenId, PillRegimenSnapshot pillRegimenSnapshot, DateTime toDate) {
        return new String[]{
                IVRMessage.YOUR_ADHERENCE_IS_NOW,
                String.valueOf(getAdherencePercentage(regimenId, toDate, pillRegimenSnapshot.getScheduledDosagesTotalCount(toDate))),
                IVRMessage.PERCENT
        };
    }
}