package org.motechproject.tama.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class IVRMessage {
    @Autowired
    @Qualifier("ivrProperties")
    Properties bundle;

    public enum Key {
        TAMA_SIGNATURE_MUSIC_URL,
        TAMA_IVR_ASK_FOR_PIN,
        TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE,
        TAMA_IVR_RESPONSE_AFTER_AUTH,
        TAMA_IVR_WELCOME_MESSAGE;
    }

    public String get(Key key) {
        return (String) bundle.get(key.name());
    }
}
