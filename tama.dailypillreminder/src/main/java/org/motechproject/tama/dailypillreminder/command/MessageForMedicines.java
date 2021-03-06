package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
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
    public MessageForMedicines(AllPatients allPatients, AllClinics allClinics, DailyPillReminderService dailyPillReminderService, ClinicNameMessageBuilder clinicNameMessageBuilder) {
        super(dailyPillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        List<String> messages = new ArrayList<String>();

        Patient patient = allPatients.get(context.patientDocumentId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        return messages.toArray(new String[messages.size()]);
    }
}
