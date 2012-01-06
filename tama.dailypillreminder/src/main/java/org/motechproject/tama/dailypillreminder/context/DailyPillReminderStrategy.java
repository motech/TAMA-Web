package org.motechproject.tama.dailypillreminder.context;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderStrategy extends PillModuleStrategy {

    private PillReminderService pillReminderService;

    @Autowired
    public DailyPillReminderStrategy(PillReminderService pillReminderService, TAMACallFlowController tamaCallFlowController) {
        super(tamaCallFlowController);
        this.pillReminderService = pillReminderService;
    }

    @Override
    public boolean previousDosageCaptured(TAMAIVRContext tamaivrContext) {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContext(tamaivrContext);
        PillRegimen pillRegimen = dailyPillReminderContext.pillRegimen(pillReminderService);
        return pillRegimen.isPreviousDosageTaken(dailyPillReminderContext.callStartTime());
    }

    @Override
    public boolean isCurrentDoseTaken(TAMAIVRContext tamaivrContext) {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContext(tamaivrContext);
        PillRegimen pillRegimen = dailyPillReminderContext.pillRegimen(pillReminderService);
        return pillRegimen.isCurrentDoseTaken(dailyPillReminderContext.callStartTime());
    }
}
