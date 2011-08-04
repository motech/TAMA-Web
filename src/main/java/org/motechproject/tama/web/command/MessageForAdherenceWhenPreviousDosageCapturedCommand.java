package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.DosageInfo;
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
        String previousDosageId = new DosageInfo(ivrContext).getPreviousDosage().getDosageId();
        if (dosageAdherenceLogs.isPreviousDosageTaken(previousDosageId)) {
            return new String[]{String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, getAdherencePercentage(regimenId))};
        }

        return new String[0];
    }
}