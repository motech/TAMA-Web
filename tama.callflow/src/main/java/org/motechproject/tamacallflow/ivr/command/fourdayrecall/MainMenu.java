package org.motechproject.tamacallflow.ivr.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MainMenu implements ITreeCommand {
    @Autowired
    AllTreatmentAdvices allTreatmentAdvices;

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(tamaivrContext);
    }

    String[] executeCommand(TAMAIVRContext tamaivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(tamaivrContext.patientId());

        if (treatmentAdvice.hasMultipleDosages())
            messages.add(TamaIVRMessage.FDR_MENU_FOR_MULTIPLE_DOSAGES);
        else
            messages.add(TamaIVRMessage.FDR_MENU_FOR_SINGLE_DOSAGE);

        return messages.toArray(new String[messages.size()]);
    }
}