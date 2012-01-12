package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.util.DateUtil;

public class RecordSymptomCommand implements ITreeCommand {
    private TAMAIVRContextFactory contextFactory;
    private SymptomRecordingService symptomRecordingService;
    private String symptomFileName;

    public RecordSymptomCommand(TAMAIVRContextFactory contextFactory, SymptomRecordingService symptomRecordingService, String symptomFileName) {
        this.contextFactory = contextFactory;
        this.symptomRecordingService = symptomRecordingService;
        this.symptomFileName = symptomFileName;
    }

    @Override
    public String[] execute(Object obj) {
        TAMAIVRContext ivrContext = contextFactory.create((KooKooIVRContext) obj);
        symptomRecordingService.save(symptomFileName, ivrContext.patientId(), ivrContext.callId(), DateUtil.now());
        return new String[0];
    }
}
