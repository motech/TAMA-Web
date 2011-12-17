package org.motechproject.tamacallflow.ivr.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DosageMissedOnMultipleDays implements ITreeCommand {

    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    private TamaIVRMessage ivrMessage;

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext((KooKooIVRContext) o);

        List<String> messages = new ArrayList<String>();
        int numDaysMissed = Integer.parseInt(tamaivrContext.dtmfInput());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(tamaivrContext.patientId());

        if (treatmentAdvice.hasMultipleDosages()) {
            messages.add(TamaIVRMessage.FDR_MISSED_MULTIPLE_ON_MULTIPLE_DAYS_PART_1);
            messages.add(ivrMessage.getNumberFilename(numDaysMissed));
            messages.add(TamaIVRMessage.FDR_MISSED_MULTIPLE_ON_MULTIPLE_DAYS_PART_2);
        } else {
            messages.add(TamaIVRMessage.FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_1);
            messages.add(ivrMessage.getNumberFilename(numDaysMissed));
            messages.add(TamaIVRMessage.FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_2);
        }
        messages.add(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY);
        return messages.toArray(new String[messages.size()]);
    }
}