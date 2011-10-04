package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.util.TamaSessionUtil;
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
        IVRContext ivrContext = (IVRContext) o;
        List<String> messages = new ArrayList<String>();

        String patientId = TamaSessionUtil.getPatientId(ivrContext);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);

        if (treatmentAdvice.getDrugDosages().size() > 1)
            messages.add(TamaIVRMessage.FDR_MENU_FOR_MULTIPLE_DOSAGES);
        else
            messages.add(TamaIVRMessage.FDR_MENU_FOR_SINGLE_DOSAGE);

        messages.add(TamaIVRMessage.FDR_MISSED_ONE_DOSAGE_ON_ONE_DAY);
        messages.add(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY);
        return messages.toArray(new String[messages.size()]);
    }
}