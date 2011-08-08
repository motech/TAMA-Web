package org.motechproject.tama.web.command;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PillsDelayWarning extends BaseTreeCommand {

    private IVRMessageBuilder ivrMessageBuilder;
    private Properties properties;

    @Autowired
    PillsDelayWarning(IVRMessageBuilder ivrMessageBuilder, @Qualifier("ivrProperties") Properties properties) {
        this.ivrMessageBuilder = ivrMessageBuilder;
        this.properties = properties;
    }

    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        if (isLastReminder(ivrContext)) {
            Integer pillWindowInHours = Integer.valueOf(properties.getProperty(TAMAConstants.PILL_WINDOW));
            DateTime nextDosageTime = new PillRegimenSnapshot(ivrContext).getNextDosageTime(pillWindowInHours);
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
