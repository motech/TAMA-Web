package org.motechproject.tamacallflow.ivr.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacallflow.ivr.context.OutboxContext;
import org.motechproject.tamacallflow.ivr.context.SymptomsReportingContext;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;

public class TAMAIVRContextFactory {

    public TAMAIVRContext initialize(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = create(kooKooIVRContext);
        tamaivrContext.initialize();
        return tamaivrContext;
    }

    public TAMAIVRContext create(KooKooIVRContext kooKooIVRContext) {
        return new TAMAIVRContext(kooKooIVRContext);
    }

    public SymptomsReportingContext createSymptomReportingContext(KooKooIVRContext kooKooIVRContext) {
        return new SymptomsReportingContext(kooKooIVRContext);
    }

    public OutboxContext createOutboxContext(KooKooIVRContext kooKooIVRContext) {
        return new OutboxContext(kooKooIVRContext);
    }
}
