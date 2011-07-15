package org.motechproject.tama.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    public String get(String key) {
        return (String) properties.get(key);
    }
}
