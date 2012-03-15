package org.motechproject.tama.fourdayrecall.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWeeklyAdherenceLogs implements ITreeCommand {
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public CreateWeeklyAdherenceLogs(FourDayRecallAdherenceService fourDayRecallAdherenceService) {
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    @Override
    public String[] execute(Object o) {
        KooKooIVRContext ivrContext = (KooKooIVRContext) o;
        TAMAIVRContext tamaivrContext = new TAMAIVRContext(ivrContext);

        return executeCommand(tamaivrContext);
    }

    String[] executeCommand(TAMAIVRContext tamaivrContext) {
        String patientId = tamaivrContext.patientDocumentId();
        int numberOfDaysMissed = Integer.parseInt(tamaivrContext.dtmfInput());
        fourDayRecallAdherenceService.recordAdherence(patientId, numberOfDaysMissed);
        return new String[0];
    }
}