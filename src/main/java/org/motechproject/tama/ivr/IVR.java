package org.motechproject.tama.ivr;

import org.motechproject.tama.ivr.action.*;

public class IVR {
    public enum CallState {
        COLLECT_PIN, AUTH_SUCCESS;

        public boolean isCollectPin() {
            return this.equals(COLLECT_PIN);
        }
    }

    public enum Event {
        NEW_CALL("NewCall") {
            @Override
            public IVRAction getAction() {
                return new NewCallAction();
            }
        },
        RECORD("Record") {
            @Override
            public IVRAction getAction() {
                return new RecordAction();
            }
        },
        GOT_DTMF("GotDTMF") {
            @Override
            public IVRAction getAction() {
                return new DTMFAction();
            }
        },
        HANGUP("Hangup") {
            @Override
            public IVRAction getAction() {
                return new HangupAction();
            }
        },
        DISCONNECT("Disconnect") {
            @Override
            public IVRAction getAction() {
                return new DisconnectAction();
            }
        };

        private String value;

        Event(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public abstract IVRAction getAction();
    }

    public static class Attributes {
        public static final String CALL_STATE = "call_state";
        public static final String CALL_ID = "call_id";
        public static final String CALLER_ID = "caller_id";
        public static final String PATIENT_DOCUMENT_ID = "patient_doc_id";
        public static final String NUMBER_OF_RETRIES = "number_of_retries";
    }
}
