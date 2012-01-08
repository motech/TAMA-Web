package org.motechproject.tama.dailypillreminder.context;

import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderStrategy extends PillModuleStrategy {

    private DailyPillReminderService dailyPillReminderService;

    @Autowired
    public DailyPillReminderStrategy(DailyPillReminderService dailyPillReminderService, TAMACallFlowController tamaCallFlowController) {
        super(tamaCallFlowController);
        this.dailyPillReminderService = dailyPillReminderService;
    }

    @Override
    public boolean previousDosageCaptured(TAMAIVRContext tamaivrContext) {
        return new DailyPillReminderContext(tamaivrContext, dailyPillReminderService).isPreviousDoseTaken();
    }

    @Override
    public boolean isCurrentDoseTaken(TAMAIVRContext tamaivrContext) {
        return new DailyPillReminderContext(tamaivrContext, dailyPillReminderService).isCurrentDoseTaken();
    }
}
