package org.motechproject.tamacallflow.domain;

public enum DosageNotTakenReason {
    NOT_FEELING_WELL,
    DO_NOT_HAVE_PILLS,
    OTHER_REASON;

    public static DosageNotTakenReason from(String input) {
        switch (Integer.valueOf(input)) {
            case 1:
                return NOT_FEELING_WELL;
            case 2:
                return DO_NOT_HAVE_PILLS;
            case 3:
                return OTHER_REASON;
            default:
                return null;
        }
    }
}
