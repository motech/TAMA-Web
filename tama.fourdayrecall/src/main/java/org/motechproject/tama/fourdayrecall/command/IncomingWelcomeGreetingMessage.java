package org.motechproject.tama.fourdayrecall.command;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class IncomingWelcomeGreetingMessage extends BaseTreeCommand {

    private ClinicNameMessageBuilder clinicNameMessageBuilder;
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public IncomingWelcomeGreetingMessage(AllPatients allPatients, AllClinics allClinics, ClinicNameMessageBuilder clinicNameMessageBuilder) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        ArrayList<String> messages = new ArrayList<String>();
        if (ivrContext.hasTraversedTree(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL)) {
            return new String[0];
        }
        Patient patient =  allPatients.get(ivrContext.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        return messages.toArray(new String[messages.size()]);
    }
}