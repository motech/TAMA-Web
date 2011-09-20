package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdherenceCommand extends BaseTreeCommand {

    private AllDosageAdherenceLogs logs;

    @Autowired
    public UpdateAdherenceCommand(AllDosageAdherenceLogs logs) {
        this.logs = logs;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        DosageStatus newStatus = DosageStatus.from(ivrContext.ivrRequest().getInput());
        String dosageId = getDosageId(ivrContext);

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(dosageId, DateUtil.today());
        DosageAdherenceLog newLog = new DosageAdherenceLog(TamaSessionUtil.getPatientId(ivrContext),
        		TamaSessionUtil.getRegimenIdFrom(ivrContext),
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

    protected String getDosageId(IVRContext ivrContext) {
        return TamaSessionUtil.getDosageIdFrom(ivrContext);
    }
}
