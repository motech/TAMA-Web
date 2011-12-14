package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceCommand extends BaseTreeCommand {

    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public UpdateAdherenceCommand(PillReminderService pillReminderService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        super(pillReminderService);
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        DosageStatus newStatus = DosageStatus.from(ivrContext.dtmfInput());
        dailyReminderAdherenceService.recordAdherence(ivrContext.patientId(),
                                                      pillRegimenResponse(ivrContext).getPillRegimenId(),
                                                      getDose(ivrContext),
                                                      newStatus,
                                                      ivrContext.callStartTime());

        return new String[0];
    }

    protected Dose getDose(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getCurrentDose();
    }

    protected String getDosageId(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getCurrentDose().getDosageId();
    }
}
