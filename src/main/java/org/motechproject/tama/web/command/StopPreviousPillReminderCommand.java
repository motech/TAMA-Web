package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPreviousPillReminderCommand extends StopTodaysRemindersCommand {
    @Autowired
    public StopPreviousPillReminderCommand(PillReminderService pillReminderService) {
        super(pillReminderService);
    }

    protected LocalDate getLastCaptureDate(IVRContext ivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        return pillRegimenSnapshot.getPreviousDosage().getDosageDate();
    }

    @Override
    protected String getDosageId(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).getPreviousDosage().getDosageId();
    }
}
