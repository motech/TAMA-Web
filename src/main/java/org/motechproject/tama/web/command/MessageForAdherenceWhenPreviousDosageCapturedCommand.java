package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends DosageAdherenceCommand {

    public MessageForAdherenceWhenPreviousDosageCapturedCommand() {
    }

    public MessageForAdherenceWhenPreviousDosageCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage) {
        super(allDosageAdherenceLogs);
        this.ivrMessage = ivrMessage;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = TamaSessionUtil.getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return getAdherenceMessage(regimenId, pillRegimenSnapshot);
        }
        return new String[0];
    }

}