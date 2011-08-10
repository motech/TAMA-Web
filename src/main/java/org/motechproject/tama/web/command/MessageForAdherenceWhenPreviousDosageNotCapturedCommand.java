package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageNotCapturedCommand extends DosageAdherenceCommand {
    private DateTime toDate;

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand() {
        toDate = DateUtil.now();
    }

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand(DosageAdherenceLogs dosageAdherenceLogs, DateTime toDate) {
        super(dosageAdherenceLogs);
        this.toDate = toDate;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        return getAdherenceMessage(regimenId, pillRegimenSnapshot, toDate);
    }
}