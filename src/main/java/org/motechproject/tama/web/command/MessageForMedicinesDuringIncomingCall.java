package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageForMedicinesDuringIncomingCall extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public MessageForMedicinesDuringIncomingCall(AllPatients allPatients, AllClinics allClinics) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        ArrayList<String> messages = new ArrayList<String>();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        Patient patient = allPatients.get(TamaSessionUtil.getPatientId(ivrContext));
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(String.format("welcome_to_%s", clinic.getName()));
        if(pillRegimenSnapshot.isTimeToTakeCurrentPill()){
            messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL);
        }else{
            messages.add(TamaIVRMessage.NOT_REPORTED_IF_TAKEN);
        }
        for (String medicine : getMedicines(ivrContext)) {
            messages.add(medicine);
        }
        messages.add(TamaIVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }

    private List<String> getMedicines(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).medicinesForCurrentDosage();
    }
}
