package org.motechproject.tama.web.command;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class PillsDelayWarning extends BaseTreeCommand {

    private IVRDayMessageBuilder ivrDayMessageBuilder;

    private IVRMessage ivrMessage;

    private Properties properties;

    @Autowired
    public PillsDelayWarning(IVRDayMessageBuilder ivrDayMessageBuilder, IVRMessage ivrMessage,  @Qualifier("ivrProperties")Properties properties) {
        this.ivrDayMessageBuilder = ivrDayMessageBuilder;
        this.ivrMessage = ivrMessage;
        this.properties = properties;
    }

    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        if (isLastReminder(ivrContext)) {
            DateTime nextDosageTime = new PillRegimenSnapshot(ivrContext).getNextDosageTime();
            List<String> messages = new ArrayList<String>();
            messages.add(IVRMessage.LAST_REMINDER_WARNING);
            messages.addAll(ivrDayMessageBuilder.getMessageForNextDosage(nextDosageTime));
            messages.add(IVRMessage.LAST_REMINDER_WARNING_PADDING);
            return  messages.toArray(new String[messages.size()]);
        }
        return new String[]{
                IVRMessage.PLEASE_TAKE_DOSE,
                ivrMessage.getNumberFilename(Integer.valueOf((String) properties.get(TAMAConstants.RETRY_INTERVAL))),
                IVRMessage.CALL_AFTER_SOME_TIME
        };
    }

    private boolean isLastReminder(IVRContext ivrContext) {
        int timesSent = getTimesSent(ivrContext);
        int totalTimesToSend = getTotalTimesToSend(ivrContext);
        return timesSent == totalTimesToSend;
    }
}
