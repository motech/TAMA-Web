package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KooKooIVRContext;

public class SymptomsReportingContextWrapperFactory {

    public SymptomsReportingContextWrapper create(KooKooIVRContext kooKooIVRContext) {
        return new SymptomsReportingContextWrapper(kooKooIVRContext);
    }

}
