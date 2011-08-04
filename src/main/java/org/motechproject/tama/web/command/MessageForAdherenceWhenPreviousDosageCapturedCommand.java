package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends DosageAdherenceCommand {

    private LocalDate toDate;

    public MessageForAdherenceWhenPreviousDosageCapturedCommand() {
        toDate = DateUtility.today();
    }

    public MessageForAdherenceWhenPreviousDosageCapturedCommand(DosageAdherenceLogs dosageAdherenceLogs, LocalDate toDate) {
        super(dosageAdherenceLogs);
        this.toDate = toDate;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;

        String regimenId = getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        if (new PillRegimenSnapshot(ivrContext).isPreviousDosageTaken()) {
            return new String[]{String.format(IVRMessage.ADHERENCE_PERCENT_MESSAGE, getAdherencePercentage(regimenId, toDate, pillRegimenSnapshot.getScheduledDosagesTotalCount(toDate)))};
        }
        return new String[0];
    }
}