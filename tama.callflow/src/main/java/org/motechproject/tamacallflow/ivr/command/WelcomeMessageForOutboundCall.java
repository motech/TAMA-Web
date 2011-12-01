package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllClinics;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamadomain.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WelcomeMessageForOutboundCall extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public WelcomeMessageForOutboundCall(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        List<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(tamaivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());
        messages.add(clinic.getName());
        return messages.toArray(new String[messages.size()]);
    }
}
