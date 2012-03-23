package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.util.DateUtil;

public class RecordSymptomCommand implements ITreeCommand {
    private SymptomRecordingService symptomRecordingService;
    private PatientAlertService patientAlertService;
    private String symptomFileName;

    public RecordSymptomCommand(SymptomRecordingService symptomRecordingService, PatientAlertService patientAlertService, String symptomFileName) {
        this.symptomRecordingService = symptomRecordingService;
        this.symptomFileName = symptomFileName;
        this.patientAlertService = patientAlertService;
    }

    @Override
    public String[] execute(Object obj) {
        TAMAIVRContext ivrContext = new TAMAIVRContext((KooKooIVRContext) obj);
        setSymptomOnAlert(ivrContext);
        symptomRecordingService.save(symptomFileName, ivrContext.patientDocumentId(), ivrContext.callId(), DateUtil.now());
        return new String[0];
    }

    private void setSymptomOnAlert(TAMAIVRContext ivrContext) {
        patientAlertService.appendSymptomToAlert(ivrContext.patientDocumentId(), symptomFileName);
    }

    public String getFileName() {
        return symptomFileName;
    }
}
