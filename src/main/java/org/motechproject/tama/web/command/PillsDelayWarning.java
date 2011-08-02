package org.motechproject.tama.web.command;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRMessageBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PillsDelayWarning implements ITreeCommand {

    PillReminderService pillReminderService;
    IVRMessageBuilder ivrMessageBuilder;

    @Autowired
    PillsDelayWarning(PillReminderService pillReminderService, IVRMessageBuilder ivrMessageBuilder) {
        this.pillReminderService = pillReminderService;
        this.ivrMessageBuilder = ivrMessageBuilder;
    }

    @Override
    public String[] execute(Object context) {
        IVRContext ivrContext = (IVRContext) context;
        Map<String, String> params = ivrContext.ivrRequest().getTamaParams();
        if (isLastReminder(params)) {
            String regimenId = params.get(PillReminderCall.REGIMEN_ID);
            String dosageId = params.get(PillReminderCall.DOSAGE_ID);
            DateTime nextDosageTime = pillReminderService.getNextDosageTime(regimenId, dosageId);
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

    private boolean isLastReminder(Map<String, String> params) {
        int timesSent = Integer.parseInt(params.get(PillReminderCall.TIMES_SENT));
        int totalTimesToSend = Integer.parseInt(params.get(PillReminderCall.TOTAL_TIMES_TO_SEND));
        return timesSent == totalTimesToSend - 1;
    }
}
