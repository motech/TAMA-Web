package org.motechproject.tama.web.command;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageForMedicines extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public MessageForMedicines(AllPatients allPatients, AllClinics allClinics) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        ArrayList<String> messages = new ArrayList<String>();

        Patient patient = allPatients.get(ivrContext.ivrSession().getPatientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinic.getName());
        messages.add(IVRMessage.ITS_TIME_FOR_THE_PILL);

        for (String medicine : getMedicines(ivrContext)) {
            messages.add(medicine);
        }
        messages.add(IVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }

    private List<String> getMedicines(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).medicinesForCurrentDosage();
    }
}
