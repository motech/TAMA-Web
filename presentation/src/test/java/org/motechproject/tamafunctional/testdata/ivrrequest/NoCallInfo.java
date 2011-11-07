package org.motechproject.tamafunctional.testdata.ivrrequest;

public class NoCallInfo implements CallInfo {
    @Override
    public String asQueryParameter() {
        return "";
    }

    @Override
    public CallInfo outgoingCall() {
        return this;
    }

    @Override
    public String appendDataMapTo(String url) {
        return url;
    }
}
