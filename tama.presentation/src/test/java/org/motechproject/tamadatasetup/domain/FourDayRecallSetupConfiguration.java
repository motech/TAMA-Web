package org.motechproject.tamadatasetup.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class FourDayRecallSetupConfiguration extends DataSetupConfiguration {
    @Autowired
    public FourDayRecallSetupConfiguration(@Qualifier("fourDayRecallDataSetup") Properties properties) {
        super(properties);
    }

    public String patientsDocumentId() {
        return stringValue("patientsDocumentId");
    }

    public String phoneNumber() {
        return stringValue("phoneNumber");
    }

    public String pinNumber() {
        return stringValue("pinNumber");
    }

    public String adherenceResponse() {
        return stringValue("adherenceResponse");
    }
}
