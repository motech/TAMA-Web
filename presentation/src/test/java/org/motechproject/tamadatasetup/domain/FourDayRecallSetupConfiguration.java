package org.motechproject.tamadatasetup.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class FourDayRecallSetupConfiguration {
    private Properties properties;

    @Autowired
    public FourDayRecallSetupConfiguration(@Qualifier("fourDayRecallDataSetup") Properties properties) {
        this.properties = properties;
    }

    public String patientsDocumentId() {
        return null;
    }

    public String phoneNumber() {
        return null;
    }

    public String pinNumber() {
        return null;
    }

    public String weeklyPattern() {
        return null;
    }
}
