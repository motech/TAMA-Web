package org.motechproject.tamacommon;

public class TamaException extends RuntimeException {
    public TamaException(String s) {
        super(s);
    }

    public TamaException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
