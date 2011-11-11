package org.motechproject.tama.web.command.callforwarding;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.SymptomsReportingContext;

public class DialStateCommand implements ITreeCommand {

    @Override
    public String[] execute(Object o) {
        SymptomsReportingContext symptomsReportingContext = new SymptomsReportingContext((KooKooIVRContext) o);
        symptomsReportingContext.startCall();
        return new String[0];
    }
}
