package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;
import org.motechproject.tama.symptomreporting.factory.SymptomReportingContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DialStateCommand implements ITreeCommand {

    private SymptomReportingService symptomReportingService;
    private SymptomReportingContextFactory symptomReportingContextFactory;

    @Autowired
    public DialStateCommand(SymptomReportingService symptomReportingService) {
        this(symptomReportingService, new SymptomReportingContextFactory());
    }

    public DialStateCommand(SymptomReportingService symptomReportingService, SymptomReportingContextFactory symptomReportingContextFactory) {
        this.symptomReportingService = symptomReportingService;
        this.symptomReportingContextFactory = symptomReportingContextFactory;
    }

    @Override
    public String[] execute(Object o) {
        SymptomsReportingContext symptomsReportingContext = symptomReportingContextFactory.create((KooKooIVRContext) o);
        symptomsReportingContext.startCall();
        symptomReportingService.smsOTCAdviceToAllClinicians(symptomsReportingContext.patientDocumentId(), symptomsReportingContext.callDetailRecordId());
        return new String[0];
    }
}
