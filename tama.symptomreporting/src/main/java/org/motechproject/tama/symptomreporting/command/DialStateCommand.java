package org.motechproject.tama.symptomreporting.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;
import org.springframework.stereotype.Component;

@Component
public class DialStateCommand implements ITreeCommand {

    @Override
    public String[] execute(Object o) {
        SymptomsReportingContext symptomsReportingContext = new SymptomsReportingContext((KooKooIVRContext) o);
        symptomsReportingContext.startCall();
        return new String[0];
    }
}
