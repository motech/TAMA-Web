package org.motechproject.tama.fourdayrecall.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeeklyAdherencePercentage implements ITreeCommand {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public WeeklyAdherencePercentage(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, FourDayRecallAdherenceService fourDayRecallAdherenceService, FourDayRecallDateService fourDayRecallDateService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
        this.fourDayRecallDateService = fourDayRecallDateService;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext ivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(ivrContext);
    }

    String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> messages = new ArrayList<String>();

        String patientDocumentId = ivrContext.patientDocumentId();

        final int numDaysMissed = Integer.parseInt(ivrContext.dtmfInput());
        int currentWeekAdherencePercentage = fourDayRecallAdherenceService.adherencePercentageFor(numDaysMissed);
        boolean falling = fourDayRecallAdherenceService.isAdherenceFalling(numDaysMissed, patientDocumentId);

        messages.add(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS);
        messages.add(TamaIVRMessage.getNumberFilename(currentWeekAdherencePercentage));
        messages.add(TamaIVRMessage.FDR_PERCENT);

        if (!fourDayRecallDateService.isFirstTreatmentWeek(allPatients.get(patientDocumentId), allTreatmentAdvices.currentTreatmentAdvice(patientDocumentId))) {
            addTrendMessages(messages, currentWeekAdherencePercentage, falling);
        }

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
