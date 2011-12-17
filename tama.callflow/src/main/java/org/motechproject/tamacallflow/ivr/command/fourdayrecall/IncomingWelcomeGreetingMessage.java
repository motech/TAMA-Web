package org.motechproject.tamacallflow.ivr.command.fourdayrecall;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.command.BaseTreeCommand;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.decisiontree.TAMATreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class IncomingWelcomeGreetingMessage extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public IncomingWelcomeGreetingMessage(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();

        Patient patient = allPatients.get(ivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        if (ivrContext.hasTraversedTree(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL)) {
            return new String[0];
        }
        messages.add(String.format("welcome_to_%s", clinic.getName()));
        return messages.toArray(new String[messages.size()]);
    }
}