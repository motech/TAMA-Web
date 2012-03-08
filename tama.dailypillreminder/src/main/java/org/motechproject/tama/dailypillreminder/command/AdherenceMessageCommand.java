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

import java.util.ArrayList;
import java.util.List;

@Component
public class AdherenceMessageCommand extends AdherenceCommand {

    protected AllDosageAdherenceLogs allDosageAdherenceLogs;
    protected DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    protected DailyPillReminderAdherenceService dailyReminderAdherenceService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AdherenceMessageCommand(AllDosageAdherenceLogs allDosageAdherenceLogs,
                                   DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService,
                                   DailyPillReminderAdherenceService dailyReminderAdherenceService,
                                   DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService, dailyReminderAdherenceService);
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    protected String[] getAdherenceMessage(DailyPillReminderContext context) {
        List<String> message = new ArrayList<String>();
        message.add(TamaIVRMessage.YOUR_ADHERENCE_IS_NOW);
        message.addAll(super.adherenceMessage(context.patientDocumentId(), context.callStartTime()));
        return message.toArray(new String[0]);
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        return getAdherenceMessage(context);
    }
}