package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageForMedicinesDuringIncomingCall extends DailyPillReminderTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public MessageForMedicinesDuringIncomingCall(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(context.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        if (!context.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM)) {
            messages.add(String.format("welcome_to_%s", clinic.getName()));
        }
        PillRegimenSnapshot pillRegimenSnapshot = pillRegimenSnapshot(context);
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
