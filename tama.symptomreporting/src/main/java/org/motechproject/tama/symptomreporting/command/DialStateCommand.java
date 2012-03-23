package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;
import org.motechproject.tama.symptomreporting.factory.SymptomReportingContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DialStateCommand implements ITreeCommand {

    private SymptomReportingService symptomReportingService;
    private SymptomReportingContextFactory symptomReportingContextFactory;
    private PatientAlertService patientAlertService;

    @Autowired
    public DialStateCommand(SymptomReportingService symptomReportingService, PatientAlertService patientAlertService) {
        this(symptomReportingService, new SymptomReportingContextFactory(), patientAlertService);
    }

    public DialStateCommand(SymptomReportingService symptomReportingService, SymptomReportingContextFactory symptomReportingContextFactory, PatientAlertService patientAlertService) {
        this.symptomReportingService = symptomReportingService;
        this.symptomReportingContextFactory = symptomReportingContextFactory;
        this.patientAlertService = patientAlertService;
    }

    @Override
    public String[] execute(Object o) {
        SymptomsReportingContext symptomsReportingContext = symptomReportingContextFactory.create((KooKooIVRContext) o);
        symptomsReportingContext.startCall();
        symptomReportingService.smsOTCAdviceToAllClinicians(symptomsReportingContext.patientDocumentId(), symptomsReportingContext.callDetailRecordId());
        patientAlertService.setConnectedToDoctorStatusOnSymptomsReportingAlert(symptomsReportingContext.patientDocumentId(), TAMAConstants.ReportedType.No);
        return new String[0];
    }
}
