package org.motechproject.tama.common;

public class NoAdherenceRecordedException extends Exception {

    public NoAdherenceRecordedException(String s) {
        super(s);
    }

    public NoAdherenceRecordedException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
