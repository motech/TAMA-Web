package org.motechproject.tama.ivr;

public enum IVREvent {
    NEW_CALL("NewCall"), RECORD("Record"), GOT_DTMF("GotDTMF"), HANGUP("Hangup"), DISCONNECT("Disconnect"), DIAL("Dial");

    private String key;

    IVREvent(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static IVREvent keyOf(String key) {
        for (IVREvent event : values())
            if (event.is(key)) return event;
        return null;
    }

    private boolean is(String key) {
        return this.key.equalsIgnoreCase(key);
    }
}
