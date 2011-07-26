package org.motechproject.tama.ivr;

public enum IVRCallState {
    COLLECT_PIN, AUTH_SUCCESS, COLLECT_PILL_REMINDER;

    public boolean isCollectPin() {
        return this.equals(COLLECT_PIN);
    }
}
