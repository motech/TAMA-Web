package org.motechproject.tama.ivr;

public enum IVRCallState {
    COLLECT_PIN, AUTH_SUCCESS, COLLECT_DOSE_RESPONSE;

    public boolean isCollectPin() {
        return this.equals(COLLECT_PIN);
    }

    public boolean isCollectDoseResponse() {
        return this.equals(COLLECT_DOSE_RESPONSE);
    }
}
