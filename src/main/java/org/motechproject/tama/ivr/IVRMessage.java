package org.motechproject.tama.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String SIGNATURE_MUSIC_URL = "signature_music";
    public static final String CONTENT_LOCATION_URL = "content.location.url";
    public static final String PILL_REMINDER_RESPONSE_MENU = "pill_reminder_menu";
    public static final String YOU_ARE_SUPPOSED_TO_TAKE = "you_are_supposed_to_take";
    public static final String WAV = ".wav";
    private Properties properties;

    @Autowired
    public IVRMessage(@Qualifier("ivrProperties") Properties properties) {
        this.properties = properties;
    }

    public String get(String key) {
        return (String) properties.get(key.toLowerCase());
    }

    public String getWav(String key) {
        String file = get(key) != null ? get(key) : key;
        return properties.get(CONTENT_LOCATION_URL) + file + WAV;
    }

}
