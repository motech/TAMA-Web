package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopTodaysRemindersCommand extends BaseTreeCommand {
    private PillReminderService pillReminderService;

    @Autowired
    public StopTodaysRemindersCommand(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;

        pillReminderService.dosageStatusKnown(getRegimenIdFrom(ivrContext), getDosageId(ivrContext), getLastCaptureDate(ivrContext));
        return new String[0];
    }

    protected LocalDate getLastCaptureDate(IVRContext ivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        return pillRegimenSnapshot.getCurrentDosage().getDosageDate();
    }

    protected String getDosageId(IVRContext ivrContext) {
        return getDosageIdFrom(ivrContext);
    }
}


