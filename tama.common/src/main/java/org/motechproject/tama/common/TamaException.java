package org.motechproject.tama.common;

public class TamaException extends RuntimeException {
    public TamaException(String s) {
        super(s);
    }

    public TamaException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
