package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePreviousPillAdherenceCommand extends UpdateAdherenceCommand {

    @Autowired
    public UpdatePreviousPillAdherenceCommand(PillReminderService pillReminderService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        super(pillReminderService, dailyReminderAdherenceService);
    }

    @Override
    protected String getDosageId(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getPreviousDose().getDosageId();
    }

    @Override
    protected Dose getDose(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getPreviousDose();
    }
}
