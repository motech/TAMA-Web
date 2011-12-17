package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.domain.DeclinedDosageLog;
import org.motechproject.tamacallflow.domain.DosageAdherenceLog;
import org.motechproject.tamacallflow.domain.DosageNotTakenReason;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordDeclinedDosageReasonCommand extends BaseTreeCommand {
    private AllDosageAdherenceLogs logs;

    @Autowired
    public RecordDeclinedDosageReasonCommand(AllDosageAdherenceLogs logs, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.logs = logs;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        DosageNotTakenReason reason = DosageNotTakenReason.from(ivrContext.dtmfInput());

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(ivrContext.dosageId(), DateUtil.today());
        if (todayLog != null) {
            DeclinedDosageLog newLog = new DeclinedDosageLog(todayLog, reason);
            logs.update(newLog.getAdherenceLog());
        }

        return new String[0];
    }
}