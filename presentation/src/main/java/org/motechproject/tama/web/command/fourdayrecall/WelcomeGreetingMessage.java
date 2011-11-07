package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.web.command.BaseTreeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class WelcomeGreetingMessage extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public WelcomeGreetingMessage(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();

        Patient patient = allPatients.get(ivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinic.getName());
        return messages.toArray(new String[messages.size()]);
    }
}