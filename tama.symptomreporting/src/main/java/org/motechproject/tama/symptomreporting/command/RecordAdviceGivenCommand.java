package org.motechproject.tama.symptomreporting.command;

import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;

public class RecordAdviceGivenCommand extends BaseTreeCommand {
    
    private SymptomRecordingService symptomRecordingService;

    private String adviceNodeName;

    public RecordAdviceGivenCommand(SymptomRecordingService symptomRecordingService, String adviceNodeName) {
        this.symptomRecordingService = symptomRecordingService;
        this.adviceNodeName = adviceNodeName;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        symptomRecordingService.saveAdviceGiven(tamaivrContext.callId(), adviceNodeName);
        return new String[0];
    }

    public String getAdviceNodeName() {
        return adviceNodeName;
    }
}
