package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.util.DateUtil;

public class RecordSymptomCommand implements ITreeCommand {
    private SymptomRecordingService symptomRecordingService;
    private String symptomFileName;

    public RecordSymptomCommand(SymptomRecordingService symptomRecordingService, String symptomFileName) {
        this.symptomRecordingService = symptomRecordingService;
        this.symptomFileName = symptomFileName;
    }

    @Override
    public String[] execute(Object obj) {
        TAMAIVRContext ivrContext = new TAMAIVRContext((KooKooIVRContext) obj);
        symptomRecordingService.save(symptomFileName, ivrContext.patientDocumentId(), ivrContext.callId(), DateUtil.now());
        return new String[0];
    }

    public String getFileName() {
	return symptomFileName;
    }
}
