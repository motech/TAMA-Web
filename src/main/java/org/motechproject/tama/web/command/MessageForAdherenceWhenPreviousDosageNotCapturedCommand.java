package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageNotCapturedCommand extends DosageAdherenceCommand {

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand() {
    }

    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, IVRMessage ivrMessage) {
        super(allDosageAdherenceLogs);
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        return getAdherenceMessage(regimenId, pillRegimenSnapshot);
    }
}