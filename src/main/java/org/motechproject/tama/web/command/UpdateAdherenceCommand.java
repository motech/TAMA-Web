package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateAdherenceCommand implements ITreeCommand {

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
        Map<String, String> params = ivrRequest.getTamaParams();

        DosageAdherenceLog log = new DosageAdherenceLog(ivrSession.get(IVRCallAttribute.PATIENT_DOC_ID),
                params.get(PillReminderCall.REGIMEN_ID),
                params.get(PillReminderCall.DOSAGE_ID),
                DosageStatus.from(ivrRequest.getInput()));
        logs.add(log);

        return new String[0];
    }
}
