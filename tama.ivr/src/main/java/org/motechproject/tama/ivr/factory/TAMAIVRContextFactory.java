package org.motechproject.tama.ivr.factory;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public class TAMAIVRContextFactory {

    public TAMAIVRContext initialize(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = create(kooKooIVRContext);
        tamaivrContext.initialize();
        return tamaivrContext;
    }

    public TAMAIVRContext create(KooKooIVRContext kooKooIVRContext) {
        return new TAMAIVRContext(kooKooIVRContext);
    }
}
