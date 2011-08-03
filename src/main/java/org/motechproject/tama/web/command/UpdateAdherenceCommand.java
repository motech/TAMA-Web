package org.motechproject.tama.web.command;

import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.repository.DosageAdherenceLogs;
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
        IVRSession ivrSession = ivrContext.ivrSession();
        IVRRequest ivrRequest = ivrContext.ivrRequest();

        DosageAdherenceLog log = new DosageAdherenceLog(ivrSession.get(IVRCallAttribute.PATIENT_DOC_ID),
                getRegimenIdFrom(ivrContext),
                getDosageIdFrom(ivrContext),
                DosageStatus.from(ivrRequest.getInput()));
        logs.add(log);

        return new String[0];
    }
}
