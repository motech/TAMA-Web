package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuspendAdherenceCallsCommand implements ITreeCommand {
    private TAMAIVRContextFactory contextFactory;
    private PatientService patientService;

    @Autowired
    public SuspendAdherenceCallsCommand(PatientService patientService) {
        this(patientService, new TAMAIVRContextFactory());
    }

    public SuspendAdherenceCallsCommand(PatientService patientService, TAMAIVRContextFactory contextFactory) {
        this.patientService = patientService;
        this.contextFactory = contextFactory;
    }

    @Override
    public String[] execute(Object obj) {
        TAMAIVRContext ivrContext = contextFactory.create((KooKooIVRContext) obj);
        patientService.suspend(ivrContext.patientId());
        return new String[0];
    }
}
