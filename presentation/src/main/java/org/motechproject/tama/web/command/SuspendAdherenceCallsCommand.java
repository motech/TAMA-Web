package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Status;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuspendAdherenceCallsCommand implements ITreeCommand {
    private TAMAIVRContextFactory contextFactory;
    private AllPatients allPatients;
    private PatientService patientService;

    public SuspendAdherenceCallsCommand(AllPatients allPatients, PatientService patientService, TAMAIVRContextFactory contextFactory) {
        this.allPatients = allPatients;
        this.patientService = patientService;
        this.contextFactory = contextFactory;
    }

    @Autowired
    public SuspendAdherenceCallsCommand(AllPatients allPatients, PatientService patientService) {
        this(allPatients, patientService, new TAMAIVRContextFactory());
    }

    @Override
    public String[] execute(Object obj) {
        TAMAIVRContext ivrContext = contextFactory.create((KooKooIVRContext) obj);
        final Patient patient = ivrContext.patient(allPatients);
        patient.setStatus(Status.Suspended);
        patientService.update(patient);
        return new String[0];
    }
}
