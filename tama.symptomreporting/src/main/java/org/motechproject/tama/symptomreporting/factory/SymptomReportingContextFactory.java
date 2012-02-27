package org.motechproject.tama.symptomreporting.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;

public class SymptomReportingContextFactory {

    public SymptomsReportingContext create(KooKooIVRContext kooKooIVRContext) {
        return new SymptomsReportingContext(kooKooIVRContext);
    }

}
