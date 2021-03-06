package org.motechproject.tama.dailypillreminder.domain;

public enum DosageStatus {
    TAKEN,
    NOT_TAKEN,
    WILL_TAKE_LATER,
    NOT_RECORDED;

    public static DosageStatus from(String input) {
        switch (Integer.valueOf(input)) {
            case 1:
                return TAKEN;
            case 2:
                return WILL_TAKE_LATER;
            case 3:
                return NOT_TAKEN;
            default:
                return NOT_RECORDED;
        }
    }

    public boolean wasReported(){
        return this == TAKEN || this == NOT_TAKEN;
    }

    public boolean wasNotReported(){
        return !wasReported();
    }
}