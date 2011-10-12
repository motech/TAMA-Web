package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.service.FourDayRecallService;
import org.motechproject.tama.util.TamaSessionUtil;
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
        IVRContext ivrContext = (IVRContext) o;
        List<String> messages = new ArrayList<String>();

        String patientId = TamaSessionUtil.getPatientId(ivrContext);

        int currentWeekAdherencePercentage = fourDayRecallService.adherencePercentageFor(Integer.parseInt(ivrContext.ivrRequest().getData()));
        int previousWeekAdherencePercentage = fourDayRecallService.adherencePercentageForPreviousWeek(patientId);
        boolean falling = currentWeekAdherencePercentage < previousWeekAdherencePercentage;

        messages.add(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS);
        messages.add(ivrMessage.getNumberFilename(currentWeekAdherencePercentage));
        messages.add(TamaIVRMessage.FDR_PERCENT);

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

        return messages.toArray(new String[messages.size()]);
    }
}
