package org.motechproject.tama.domain;

import java.security.PublicKey;

public enum DosageStatus {
    TAKEN,
    NOT_TAKEN,
    WILL_TAKE_LATER;

    public static DosageStatus from(String input) {
        switch(Integer.valueOf(input)){
            case 1: return TAKEN;
            case 2: return NOT_TAKEN;
            case 3: return WILL_TAKE_LATER;
            default:return null;
        }
    }
}