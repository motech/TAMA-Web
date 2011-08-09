package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.util.DateUtil;
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

        LocalDate dosageCaptureDate = shouldStopTodaysDosage(ivrContext) ? DateUtil.today() : DateUtil.today().minusDays(1);
        pillReminderService.dosageStatusKnown(getRegimenIdFrom(ivrContext), getDosageId(ivrContext), dosageCaptureDate);
        return new String[0];
    }

    protected boolean shouldStopTodaysDosage(IVRContext ivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DosageResponse currentDosage = pillRegimenSnapshot.getCurrentDosage();
        return pillRegimenSnapshot.isTodaysDosage(currentDosage);
    }

    protected String getDosageId(IVRContext ivrContext) {
        return getDosageIdFrom(ivrContext);
    }
}


