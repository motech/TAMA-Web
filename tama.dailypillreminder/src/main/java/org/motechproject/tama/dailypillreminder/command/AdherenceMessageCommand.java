package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceMessageCommand extends DailyPillReminderTreeCommand {

    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    protected TamaIVRMessage ivrMessage;
    protected DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    protected DailyPillReminderAdherenceService dailyReminderAdherenceService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AdherenceMessageCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage tamaIVRMessage, DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, DailyPillReminderAdherenceService dailyReminderAdherenceService, DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
        this.ivrMessage = tamaIVRMessage;
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    protected String[] getAdherenceMessage(DailyPillReminderContext context) {
        int adherencePercentage = 0;
        try {
            adherencePercentage = (int) (dailyReminderAdherenceService.getAdherencePercentage(context.patientDocumentId(), context.currentDose().getDoseTime()));
        } catch (NoAdherenceRecordedException ignored) {
            logger.info("No Adherence records found!");
        }
        return new String[]{
                TamaIVRMessage.YOUR_ADHERENCE_IS_NOW,
                ivrMessage.getNumberFilename(adherencePercentage),
                TamaIVRMessage.PERCENT
        };
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        return getAdherenceMessage(context);
    }
}