package org.motechproject.tamadatasetup.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;

import java.util.Properties;

public abstract class DataSetupConfiguration {
    private Properties properties;

    protected DataSetupConfiguration(Properties properties) {
        this.properties = properties;
    }

    protected int intValue(String key, int defaultValue) {
        String stringValue = stringValue(key);
        return StringUtils.isEmpty(stringValue) ? defaultValue : Integer.parseInt(stringValue);
    }

    protected int intValue(String key) {
        return Integer.parseInt(stringValue(key));
    }

    protected String stringValue(String key) {
        return properties.getProperty(key);
    }

    protected Time timeValue(String key, Time defaultValue) {
        String stringValue = stringValue(key);
        if (StringUtils.isEmpty(stringValue)) return defaultValue;
        return Time.parseTime(stringValue, ":");
    }

    protected Time timeValue(String key) {
        return Time.parseTime(stringValue(key), ":");
    }

    protected boolean booleanValue(String key) {
        String value = stringValue(key);
        if (value == null) throw new NullPointerException("Key:" + key);
        return Boolean.parseBoolean(value);
    }

    protected LocalDate dateValue(String key) {
        return LocalDate.parse(stringValue(key));
    }
}
