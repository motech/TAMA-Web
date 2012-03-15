package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class IncomingWelcomeMessage extends DailyPillReminderTreeCommand {

    private AllPatients allPatients;
    private AllClinics allClinics;
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    @Autowired
    public IncomingWelcomeMessage(AllPatients allPatients, AllClinics allClinics, ClinicNameMessageBuilder clinicNameMessageBuilder, DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        if (context.hasTraversedAnyTree()) {
            return new String[0];
        }
        ArrayList<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(context.patientDocumentId());
        Clinic clinic = allClinics.get(patient.getClinic_id());
        messages.add(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        return messages.toArray(new String[messages.size()]);
    }
}
