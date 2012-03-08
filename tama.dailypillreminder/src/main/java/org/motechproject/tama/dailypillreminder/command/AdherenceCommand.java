package org.motechproject.tama.dailypillreminder.command;


import org.joda.time.DateTime;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class AdherenceCommand extends DailyPillReminderTreeCommand {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected AdherenceCommand(DailyPillReminderService dailyPillReminderService, DailyPillReminderAdherenceService dailyPillReminderAdherenceService) {
        super(dailyPillReminderService);
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        String patientId = context.patientDocumentId();
        return adherenceMessage(patientId).toArray(new String[0]);
    }

    protected List<String> adherenceMessage(String patientId) {
        return adherenceMessage(patientId, DateUtil.now());
    }

    protected List<String> adherenceMessage(String patientId, DateTime time) {
        List<String> adherenceMessage = new ArrayList<String>();
        try {
            double adherence = dailyPillReminderAdherenceService.getAdherencePercentage(patientId, time);
            adherenceMessage.add(TamaIVRMessage.getNumberFilename((int) adherence));
            adherenceMessage.add(TamaIVRMessage.PERCENT);
            return adherenceMessage;
        } catch (NoAdherenceRecordedException e) {
            logger.debug("No Adherence Records Found!");
        }
        return adherenceMessage;
    }
}
