package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.call.PillReminderCall;

public abstract class BaseTreeCommand implements ITreeCommand{

    protected String getDosageIdFrom(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).getCurrentDosage().getDosageId();
    }

    protected String getRegimenIdFrom(IVRContext ivrContext) {
        return ivrContext.ivrSession().getPillRegimen().getPillRegimenId();
    }

    protected int getTimesSent(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.TIMES_SENT).toString());
    }

    protected int getTotalTimesToSend(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getTamaParams().get(PillReminderCall.TOTAL_TIMES_TO_SEND).toString());
    }
}