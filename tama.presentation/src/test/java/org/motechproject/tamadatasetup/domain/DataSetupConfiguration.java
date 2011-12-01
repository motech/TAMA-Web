package org.motechproject.tamadatasetup.domain;

import java.util.Properties;

public abstract class DataSetupConfiguration {
    private Properties properties;

    protected DataSetupConfiguration(Properties properties) {
        this.properties = properties;
    }

    protected int intValue(String key) {
        return Integer.parseInt(stringValue(key));
    }

    protected String stringValue(String key) {
        return properties.getProperty(key);
    }
}
