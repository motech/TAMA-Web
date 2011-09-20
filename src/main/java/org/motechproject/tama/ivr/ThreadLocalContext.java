package org.motechproject.tama.ivr;

import org.motechproject.server.service.ivr.IVRContext;

public class ThreadLocalContext {
    private IVRContext ivrContext;

    public IVRContext getIvrContext() {
        return ivrContext;
    }

    public void setIvrContext(IVRContext ivrContext) {
        this.ivrContext = ivrContext;
    }
}