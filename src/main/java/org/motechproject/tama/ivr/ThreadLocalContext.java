package org.motechproject.tama.ivr;

public class ThreadLocalContext {
    private IVRContext ivrContext;

    public IVRContext getIvrContext() {
        return ivrContext;
    }

    public void setIvrContext(IVRContext ivrContext) {
        this.ivrContext = ivrContext;
    }
}