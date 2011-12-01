package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.LocalDate;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageCommand extends BaseTreeCommand {

    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    protected TamaIVRMessage ivrMessage;
    protected DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Autowired
    public AdherenceMessageCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage tamaIVRMessage, DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService) {
        super(null);
        this.ivrMessage = tamaIVRMessage;
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
    }

    protected String[] getAdherenceMessage(TAMAIVRContext ivrContext) {
        int adherencePercentage = (int) (dailyReminderAdherenceTrendService.getAdherence(ivrContext.patientId()) * 100);
        return new String[]{
                TamaIVRMessage.YOUR_ADHERENCE_IS_NOW,
                ivrMessage.getNumberFilename(adherencePercentage),
                TamaIVRMessage.PERCENT
        };
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        return getAdherenceMessage(tamaivrContext);
    }
}