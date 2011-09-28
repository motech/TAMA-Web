package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DosageAdherenceCommand extends BaseTreeCommand {
    @Autowired
    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Autowired
    protected TamaIVRMessage ivrMessage;

    protected DosageAdherenceCommand() {
    }

    public DosageAdherenceCommand(AllDosageAdherenceLogs allDosageAdherenceLogs) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
    }

    protected int getAdherencePercentage(String regimenId, int scheduledDosagesTotalCount) {
        LocalDate toDate = DateUtil.today();
        LocalDate fromDate = toDate.minusDays(TAMAConstants.DAYS_IN_FOUR_WEEKS);
        int scheduledDosagesSuccessCount = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, fromDate, toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }

    protected String[] getAdherenceMessage(String regimenId, PillRegimenSnapshot pillRegimenSnapshot) {
        return new String[]{
                TamaIVRMessage.YOUR_ADHERENCE_IS_NOW,
                ivrMessage.getNumberFilename(getAdherencePercentage(regimenId, pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks())),
                TamaIVRMessage.PERCENT
        };
    }
}