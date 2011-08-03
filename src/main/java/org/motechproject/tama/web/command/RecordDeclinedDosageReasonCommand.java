package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.domain.DeclinedDosageLog;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageNotTakenReason;
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
public class RecordDeclinedDosageReasonCommand implements ITreeCommand {
    private DosageAdherenceLogs logs;

    @Autowired
    public RecordDeclinedDosageReasonCommand(DosageAdherenceLogs logs) {
        this.logs = logs;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        IVRSession ivrSession = ivrContext.ivrSession();
        IVRRequest ivrRequest = ivrContext.ivrRequest();
        Map<String, String> params = ivrRequest.getTamaParams();
        String dosageId = params.get(PillReminderCall.DOSAGE_ID);
        DosageNotTakenReason reason = DosageNotTakenReason.from(ivrRequest.getInput());

        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(dosageId, DateUtility.today());
        if (todayLog != null) {
            DeclinedDosageLog newLog = new DeclinedDosageLog(todayLog, reason);
            logs.update(newLog.getAdherenceLog());
        }

        return new String[0];
    }
}