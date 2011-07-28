package org.motechproject.tama.ivr;

public enum IVRCallState {
    AUTH_SUCCESS,
    COLLECT_PIN,
    COLLECT_DOSE_RESPONSE,
    COLLECT_DOSE_CANNOT_BE_TAKEN;

    public boolean isCollectPin() {
        return this.equals(COLLECT_PIN);
    }

    public boolean isCollectDoseResponse() {
        return this.equals(COLLECT_DOSE_RESPONSE);
    }

    public boolean isCollectDoseCannotBeTakenResponse() {
        return this.equals(COLLECT_DOSE_CANNOT_BE_TAKEN);
    }
}
