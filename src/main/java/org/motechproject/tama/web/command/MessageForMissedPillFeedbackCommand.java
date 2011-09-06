package org.motechproject.tama.web.command;

import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.stereotype.Component;

@Component
public class MessageForMissedPillFeedbackCommand extends DosageAdherenceCommand {

    public MessageForMissedPillFeedbackCommand() {
    }

    public MessageForMissedPillFeedbackCommand(AllDosageAdherenceLogs allDosageAdherenceLogs) {
        super(allDosageAdherenceLogs);
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        String regimenId = getRegimenIdFrom(ivrContext);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        int dosagesFailureCount = allDosageAdherenceLogs.findScheduledDosagesFailureCount(regimenId);

        switch (dosagesFailureCount) {
            case 1:
                return new String[]{IVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME};
            case 2:
            case 3:
            case 4:
                return new String[]{IVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME};
            default:
                int adherencePercentage = getAdherencePercentage(regimenId, pillRegimenSnapshot.getScheduledDosagesTotalCount());
                return new String[]{getMissedPillFeedbackMessageFor(adherencePercentage)};
        }
    }

    private String getMissedPillFeedbackMessageFor(int adherencePercentage) {
        if (adherencePercentage > 90)
            return IVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90;
        else if (adherencePercentage >= 70 && adherencePercentage <= 90)
            return IVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90;

        return IVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70;
    }
}