package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.outbox.OutboxContext;

public class TAMAIVRContextFactory {

    public TAMAIVRContext initialize(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = create(kooKooIVRContext);
        tamaivrContext.initialize();
        return tamaivrContext;
    }

    public TAMAIVRContext create(KooKooIVRContext kooKooIVRContext) {
        return new TAMAIVRContext(kooKooIVRContext);
    }

    public OutboxContext createOutboxContext(KooKooIVRContext kooKooIVRContext) {
        return new OutboxContext(kooKooIVRContext);
    }
}
