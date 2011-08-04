package org.motechproject.tama.web.command;

import org.motechproject.tama.domain.DeclinedDosageLog;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageNotTakenReason;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordDeclinedDosageReasonCommand extends BaseTreeCommand {
    private DosageAdherenceLogs logs;

    @Autowired
    public RecordDeclinedDosageReasonCommand(DosageAdherenceLogs logs) {
        this.logs = logs;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        IVRRequest ivrRequest = ivrContext.ivrRequest();
        String dosageId = getDosageIdFrom(ivrContext);
        DosageNotTakenReason reason = DosageNotTakenReason.from(ivrRequest.getInput());

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(dosageId, DateUtility.today());
        if (todayLog != null) {
            DeclinedDosageLog newLog = new DeclinedDosageLog(todayLog, reason);
            logs.update(newLog.getAdherenceLog());
        }

        return new String[0];
    }
}