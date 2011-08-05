package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class MessageForAdherenceWhenPreviousDosageCapturedCommand extends DosageAdherenceCommand {

    private LocalDate toDate;

    public MessageForAdherenceWhenPreviousDosageCapturedCommand() {
        toDate = DateUtil.today();
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
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) {
            return getAdherenceMessage(regimenId, pillRegimenSnapshot, toDate);
        }
        return new String[0];
    }

}