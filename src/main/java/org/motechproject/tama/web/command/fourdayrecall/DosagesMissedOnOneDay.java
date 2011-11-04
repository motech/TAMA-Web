package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DosagesMissedOnOneDay implements ITreeCommand {
    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(tamaivrContext);
    }

    String[] executeCommand(TAMAIVRContext tamaivrContext) {
        List<String> messages = new ArrayList<String>();
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(tamaivrContext.patientId());

        if (treatmentAdvice.hasMultipleDosages())
            messages.add(TamaIVRMessage.FDR_MISSED_MULTIPLE_DOSAGES_ON_ONE_DAY);
        else
            messages.add(TamaIVRMessage.FDR_MISSED_ONE_DOSAGE_ON_ONE_DAY);

        messages.add(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY);
        return messages.toArray(new String[messages.size()]);
    }
}