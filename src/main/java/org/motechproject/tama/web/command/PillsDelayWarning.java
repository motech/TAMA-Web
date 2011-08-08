package org.motechproject.tama.web.command;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PillsDelayWarning extends BaseTreeCommand {

    private IVRMessageBuilder ivrMessageBuilder;

    @Autowired
    PillsDelayWarning(IVRMessageBuilder ivrMessageBuilder) {
        this.ivrMessageBuilder = ivrMessageBuilder;
    }

    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        if (isLastReminder(ivrContext)) {
            DateTime nextDosageTime = new PillRegimenSnapshot(ivrContext).getNextDosageTime();
            return (String[]) ArrayUtils.addAll(
                    new String[]{
                            IVRMessage.LAST_REMINDER_WARNING
                    }, ivrMessageBuilder.getWavs(nextDosageTime).toArray());
        }
        return new String[]{
                IVRMessage.PLEASE_TAKE_DOSE,
                TAMAConstants.RETRY_INTERVAL,
                IVRMessage.MINUTES
        };
    }

    private boolean isLastReminder(IVRContext ivrContext) {
        int timesSent = getTimesSent(ivrContext);
        int totalTimesToSend = getTotalTimesToSend(ivrContext);
        return timesSent == totalTimesToSend - 1;
    }
}
