package org.motechproject.tama.web.command;

import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceCommand extends BaseTreeCommand {

    private DosageAdherenceLogs logs;

    @Autowired
    public UpdateAdherenceCommand(DosageAdherenceLogs logs) {
        this.logs = logs;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        DosageStatus newStatus = DosageStatus.from(ivrContext.ivrRequest().getInput());
        String dosageId = getDosageIdFrom(ivrContext);

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(dosageId, DateUtil.today());
        DosageAdherenceLog newLog = new DosageAdherenceLog(ivrContext.ivrSession().getPatientId(),
                getRegimenIdFrom(ivrContext),
                dosageId,
                newStatus);

        if (todayLog == null) {
            logs.add(newLog);
        } else if(!todayLog.equals(newLog)) {
            todayLog.setDosageStatus(newStatus);
            logs.update(todayLog);
        }

        return new String[0];
    }
}
