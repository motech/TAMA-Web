package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DosageAdherenceCommand extends BaseTreeCommand {
    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    protected TamaIVRMessage ivrMessage;

    @Autowired
    public DosageAdherenceCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage tamaIVRMessage, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.ivrMessage = tamaIVRMessage;
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
    }

    protected int getAdherencePercentage(String regimenId, int scheduledDosagesTotalCount) {
        LocalDate toDate = DateUtil.today();
        LocalDate fromDate = toDate.minusDays(TAMAConstants.DAYS_IN_FOUR_WEEKS);
        int scheduledDosagesSuccessCount = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, fromDate, toDate);
        return scheduledDosagesSuccessCount * 100 / scheduledDosagesTotalCount;
    }

    protected String[] getAdherenceMessage(TAMAIVRContext ivrContext) {
        return new String[]{
                TamaIVRMessage.YOUR_ADHERENCE_IS_NOW,
                ivrMessage.getNumberFilename(getAdherencePercentage(pillRegimen(ivrContext).getPillRegimenId(),
                        pillRegimenSnapshot(ivrContext).getScheduledDosagesTotalCountForLastFourWeeks())),
                TamaIVRMessage.PERCENT
        };
    }
}