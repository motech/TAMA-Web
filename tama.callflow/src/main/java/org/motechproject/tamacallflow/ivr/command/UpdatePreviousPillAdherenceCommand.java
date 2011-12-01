package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePreviousPillAdherenceCommand extends UpdateAdherenceCommand {
    @Autowired
    public UpdatePreviousPillAdherenceCommand(AllDosageAdherenceLogs logs, PillReminderService pillReminderService) {
        super(logs, pillReminderService);
    }

    @Override
    protected String getDosageId(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getPreviousDosage().getDosageId();
    }

    @Override
    protected LocalDate getDosageDate(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getPreviousDosage().getDosageDate();
    }
}
