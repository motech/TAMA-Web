package org.motechproject.tama.ivr;

public class IVR {
    public enum CallState {
        COLLECT_PIN, AUTH_SUCCESS;

        public boolean isCollectPin() {
            return this.equals(COLLECT_PIN);
        }
    }

    public enum Event {
        NEW_CALL("NewCall"), RECORD("Record"), GOT_DTMF("GotDTMF"), HANGUP("Hangup"), DISCONNECT("Disconnect");

        private String key;

        Event(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }

        public static Event keyOf(String key) {
            for (Event event : values())
                if (event.is(key)) return event;
            return null;
        }

        private boolean is(String key) {
            return this.key.equalsIgnoreCase(key);
        }
    }

    public static class Attributes {
        public static final String CALL_STATE = "call_state";
        public static final String CALL_ID = "call_id";
        public static final String CALLER_ID = "caller_id";
        public static final String PATIENT_DOCUMENT_ID = "patient_doc_id";
        public static final String NUMBER_OF_ATTEMPTS = "number_of_retries";
    }

    public static class MessageKey {
        public static final String TAMA_SIGNATURE_MUSIC_URL = "signature.music.url";
        public static final String TAMA_IVR_ASK_FOR_PIN = "ask.for.pin";
        public static final String TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE = "ask.for.pin.after.failure";
        public static final String TAMA_IVR_RESPONSE_AFTER_AUTH = "ask.for.response.after.authorisation";
        public static final String TAMA_IVR_WELCOME_MESSAGE = "welcome.message";
        public static final String TAMA_IVR_REPORT_USER_NOT_FOUND = "report.user.not.found";
        public static final String TAMA_IVR_REPORT_USER_NOT_AUTHORISED = "report.user.not.authorised";
        public static final String TAMA_IVR_REMIND_FOR_PIN = "remind.for.pin";
    }
}
