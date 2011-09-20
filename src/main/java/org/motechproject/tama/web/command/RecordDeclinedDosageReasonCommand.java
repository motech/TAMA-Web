package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.DeclinedDosageLog;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageNotTakenReason;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordDeclinedDosageReasonCommand extends BaseTreeCommand {
    private AllDosageAdherenceLogs logs;

    @Autowired
    public RecordDeclinedDosageReasonCommand(AllDosageAdherenceLogs logs) {
        this.logs = logs;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        IVRRequest ivrRequest = ivrContext.ivrRequest();
        String dosageId = TamaSessionUtil.getDosageIdFrom(ivrContext);
        DosageNotTakenReason reason = DosageNotTakenReason.from(ivrRequest.getInput());

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(dosageId, DateUtil.today());
        if (todayLog != null) {
            DeclinedDosageLog newLog = new DeclinedDosageLog(todayLog, reason);
            logs.update(newLog.getAdherenceLog());
        }

        return new String[0];
    }
}