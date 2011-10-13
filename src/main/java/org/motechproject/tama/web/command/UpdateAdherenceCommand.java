package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceCommand extends BaseTreeCommand {
    private AllDosageAdherenceLogs logs;

    @Autowired
    public UpdateAdherenceCommand(AllDosageAdherenceLogs logs, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.logs = logs;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        DosageStatus newStatus = DosageStatus.from(ivrContext.dtmfInput());
        String dosageId = getDosageId(ivrContext);

        DosageAdherenceLog log = logs.findByDosageIdAndDate(dosageId, getDosageDate(ivrContext));
        DosageAdherenceLog newLog = new DosageAdherenceLog(ivrContext.patientId(), pillRegimen(ivrContext).getPillRegimenId(),
                dosageId,
                newStatus, getDosageDate(ivrContext));

        if (log == null) {
            logs.add(newLog);
        } else if (!log.equals(newLog)) {
            log.setDosageStatus(newStatus);
            logs.update(log);
        }

        return new String[0];
    }

    protected LocalDate getDosageDate(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getCurrentDosage().getDosageDate();
    }

    protected String getDosageId(TAMAIVRContext ivrContext) {
        return pillRegimenSnapshot(ivrContext).getCurrentDosage().getDosageId();
    }
}
