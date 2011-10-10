package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.web.command.BaseTreeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class WelcomeGreetingMessage extends BaseTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;

    @Autowired
    public WelcomeGreetingMessage(AllPatients allPatients, AllClinics allClinics) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
    }
    
    @Override
    public String[] execute(Object obj) {
        IVRContext ivrContext = (IVRContext) obj;
        
        ArrayList<String> messages = new ArrayList<String>();
        
        Patient patient = allPatients.get(TamaSessionUtil.getPatientId(ivrContext));
        Clinic clinic = allClinics.get(patient.getClinic_id());

        messages.add(clinic.getName());
        messages.add(TamaIVRMessage.FDR_GREETING);
        return messages.toArray(new String[messages.size()]);
    }
}