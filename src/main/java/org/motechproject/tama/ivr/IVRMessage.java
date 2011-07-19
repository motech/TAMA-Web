package org.motechproject.tama.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String TAMA_SIGNATURE_MUSIC_URL = "signature.music.url";
    public static final String TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE = "ask.for.pin.after.failure";
    public static final String TAMA_IVR_WELCOME_MESSAGE = "welcome.message";
    public static final String TAMA_IVR_REPORT_USER_NOT_FOUND = "report.user.not.found";
    public static final String TAMA_IVR_REPORT_USER_NOT_AUTHORISED = "report.user.not.authorised";
    public static final String TAMA_IVR_REMIND_FOR_PIN = "remind.for.pin";

    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    public String get(String key) {
        return (String) properties.get(key);
    }
}
