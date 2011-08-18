package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DosageAdherenceCommand extends BaseTreeCommand {
    @Autowired
    protected DosageAdherenceLogs dosageAdherenceLogs;

    protected DosageAdherenceCommand() {
    }

    public DosageAdherenceCommand(DosageAdherenceLogs dosageAdherenceLogs) {
        this.dosageAdherenceLogs = dosageAdherenceLogs;
    }

    protected int getAdherencePercentage(String regimenId, int scheduledDosagesTotalCount) {
        LocalDate toDate = DateUtil.today();
        LocalDate fromDate = toDate.minusDays(TAMAConstants.DAYS_IN_FOUR_WEEKS - 1);
        int scheduledDosagesSuccessCount = dosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, fromDate, toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }

    protected String[] getAdherenceMessage(String regimenId, PillRegimenSnapshot pillRegimenSnapshot) {
        return new String[]{
                IVRMessage.YOUR_ADHERENCE_IS_NOW,
                String.valueOf(getAdherencePercentage(regimenId, pillRegimenSnapshot.getScheduledDosagesTotalCount())),
                IVRMessage.PERCENT
        };
    }
}