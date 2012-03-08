package org.motechproject.tama.dailypillreminder.command;


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
        List<String> adherenceMessage = new ArrayList<String>();
        try {
            double adherence = dailyPillReminderAdherenceService.getAdherencePercentage(context.patientDocumentId(), DateUtil.now());
            adherenceMessage.add(TamaIVRMessage.getNumberFilename((int) adherence));
            adherenceMessage.add(TamaIVRMessage.PERCENT);
            return adherenceMessage.toArray(new String[0]);
        } catch (NoAdherenceRecordedException e) {
            logger.debug("No Adherence Records Found!");
        }
        return new String[0];
    }
}
