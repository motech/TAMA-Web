package org.motechproject.tama.dailypillreminder.domain;

public enum DosageStatus {
    TAKEN,
    NOT_TAKEN,
    WILL_TAKE_LATER;

    public static DosageStatus from(String input) {
        switch (Integer.valueOf(input)) {
            case 1:
                return TAKEN;
            case 2:
                return WILL_TAKE_LATER;
            case 3:
                return NOT_TAKEN;
            default:
                return null;
        }
    }
}