package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends DosageAdherenceCommand {

    @Autowired
    public MessageForAdherenceWhenPreviousDosageCapturedCommand(DosageAdherenceLogs dosageAdherenceLogs) {
        super(dosageAdherenceLogs);
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        if (new PillRegimenSnapshot(ivrContext).isPreviousDosageTaken()) {
            return new String[]{String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, getAdherencePercentage(regimenId))};
        }
        return new String[0];
    }
}