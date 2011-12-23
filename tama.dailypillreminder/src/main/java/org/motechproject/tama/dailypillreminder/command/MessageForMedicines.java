package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageForMedicines extends DailyPillReminderTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    @Autowired
    public MessageForMedicines(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService, ClinicNameMessageBuilder clinicNameMessageBuilder) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        List<String> messages = new ArrayList<String>();

        Patient patient = allPatients.get(context.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL);

        for (String medicine : getMedicines(context)) {
            messages.add(medicine);
        }
        messages.add(TamaIVRMessage.PILL_FROM_THE_BOTTLE);
        return messages.toArray(new String[messages.size()]);
    }

    private List<String> getMedicines(DailyPillReminderContext context) {
        return pillRegimenSnapshot(context).medicinesForCurrentDose();
    }
}
