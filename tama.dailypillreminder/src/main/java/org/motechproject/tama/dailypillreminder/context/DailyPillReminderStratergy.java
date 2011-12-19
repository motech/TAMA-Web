package org.motechproject.tama.dailypillreminder.context;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.ivr.context.PillModuleStratergy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyPillReminderStratergy extends PillModuleStratergy {

    private PillReminderService pillReminderService;

    @Autowired
    public DailyPillReminderStratergy(PillReminderService pillReminderService, TAMACallFlowController tamaCallFlowController) {
        super(tamaCallFlowController);
        this.pillReminderService = pillReminderService;
    }

    @Override
    public boolean previousDosageCaptured(TAMAIVRContext tamaivrContext) {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContext(tamaivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(dailyPillReminderContext);
        return pillRegimenSnapshot.isPreviousDosageCaptured();
    }

    @Override
    public boolean isCurrentDoseTaken(TAMAIVRContext tamaivrContext) {
        DailyPillReminderContext dailyPillReminderContext = new DailyPillReminderContext(tamaivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(dailyPillReminderContext);
        return pillRegimenSnapshot.isCurrentDoseTaken();
    }

    private PillRegimenSnapshot pillRegimenSnapshot(DailyPillReminderContext dailyPillReminderContext) {
        return dailyPillReminderContext.pillRegimenSnapshot(pillReminderService);
    }
}
