package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageNotCapturedCommand extends DosageAdherenceCommand {

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand() {
    }

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand(DosageAdherenceLogs dosageAdherenceLogs) {
        super(dosageAdherenceLogs);
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        return getAdherenceMessage(regimenId, pillRegimenSnapshot);
    }
}