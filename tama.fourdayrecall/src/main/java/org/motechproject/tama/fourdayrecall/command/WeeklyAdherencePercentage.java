package org.motechproject.tama.fourdayrecall.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeeklyAdherencePercentage implements ITreeCommand {

    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public WeeklyAdherencePercentage(FourDayRecallAdherenceService fourDayRecallAdherenceService) {
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext ivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(ivrContext);
    }

    String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> messages = new ArrayList<String>();

        final int numDaysMissed = Integer.parseInt(ivrContext.dtmfInput());
        int currentWeekAdherencePercentage = fourDayRecallAdherenceService.adherencePercentageFor(numDaysMissed);

        messages.add(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS);
        messages.add(TamaIVRMessage.getNumberFilename(currentWeekAdherencePercentage));
        messages.add(TamaIVRMessage.FDR_PERCENT);

        return messages.toArray(new String[messages.size()]);
    }
}
