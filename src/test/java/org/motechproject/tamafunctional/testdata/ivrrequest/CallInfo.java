package org.motechproject.tamafunctional.testdata.ivrrequest;

public interface CallInfo {
    String asQueryParameter();
    CallInfo outgoingCall();
    String appendDataMapTo(String url);
}
