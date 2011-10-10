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
public class DosageMissedOnMultipleDays implements ITreeCommand {

    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    private TamaIVRMessage ivrMessage;

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        List<String> messages = new ArrayList<String>();

        int numDaysMissed = Integer.parseInt(ivrContext.ivrRequest().getData());

        String patientId = TamaSessionUtil.getPatientId(ivrContext);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);

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