package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.decisiontree.TAMATreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageForMedicinesDuringIncomingCall extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public MessageForMedicinesDuringIncomingCall(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(tamaivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        if (!tamaivrContext.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM)) {
            messages.add(String.format("welcome_to_%s", clinic.getName()));
        }
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(tamaivrContext);
        if (pillRegimenSnapshot.isTimeToTakeCurrentPill()) {
            messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL);
        } else {
            messages.add(TamaIVRMessage.NOT_REPORTED_IF_TAKEN);
        }
        for (String medicine : pillRegimenSnapshot.medicinesForCurrentDose()) {
            messages.add(medicine);
        }
        messages.add(TamaIVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }
}
