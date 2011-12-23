package org.motechproject.tama.outbox.command;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WelcomeMessageForOutboundCall extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    @Autowired
    public WelcomeMessageForOutboundCall(AllPatients allPatients, AllClinics allClinics, ClinicNameMessageBuilder clinicNameMessageBuilder) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        List<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(tamaivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());
        messages.add(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        return messages.toArray(new String[messages.size()]);
    }
}
