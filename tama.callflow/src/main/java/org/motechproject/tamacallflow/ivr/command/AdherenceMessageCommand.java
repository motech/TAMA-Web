package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageCommand extends BaseTreeCommand {

    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    protected TamaIVRMessage ivrMessage;
    protected DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    protected DailyReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public AdherenceMessageCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage tamaIVRMessage, DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        super(null);
        this.ivrMessage = tamaIVRMessage;
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    protected String[] getAdherenceMessage(TAMAIVRContext ivrContext) {
        int adherencePercentage = (int) (dailyReminderAdherenceService.getAdherence(ivrContext.patientId(), DateUtil.now()) * 100);
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