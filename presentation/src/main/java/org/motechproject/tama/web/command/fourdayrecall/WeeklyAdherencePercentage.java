package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeeklyAdherencePercentage implements ITreeCommand {

    private TamaIVRMessage ivrMessage;
    private FourDayRecallService fourDayRecallService;

    @Autowired
    public WeeklyAdherencePercentage(TamaIVRMessage ivrMessage, FourDayRecallService fourDayRecallService) {
        this.ivrMessage = ivrMessage;
        this.fourDayRecallService = fourDayRecallService;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext ivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(ivrContext);
    }

    String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> messages = new ArrayList<String>();

        String patientId = ivrContext.patientId();

        final int numDaysMissed = Integer.parseInt(ivrContext.dtmfInput());
        int currentWeekAdherencePercentage = fourDayRecallService.adherencePercentageFor(numDaysMissed);
        boolean falling = fourDayRecallService.isAdherenceFalling(numDaysMissed, patientId);

        messages.add(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS);
        messages.add(ivrMessage.getNumberFilename(currentWeekAdherencePercentage));
        messages.add(TamaIVRMessage.FDR_PERCENT);

        if (!fourDayRecallService.isAdherenceBeingCapturedForFirstWeek(patientId))
            addTrendMessages(messages, currentWeekAdherencePercentage, falling);

        return messages.toArray(new String[messages.size()]);
    }

    private void addTrendMessages(List<String> messages, int currentWeekAdherencePercentage, boolean falling) {
        if (currentWeekAdherencePercentage > 90) {
            messages.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
        } else if (currentWeekAdherencePercentage > 70) {
            if (falling) {
                messages.add(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING);
            } else {
                messages.add(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
            }
        } else {
            if (falling) {
                messages.add(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING);
            } else {
                messages.add(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING);
            }
        }
    }
}
