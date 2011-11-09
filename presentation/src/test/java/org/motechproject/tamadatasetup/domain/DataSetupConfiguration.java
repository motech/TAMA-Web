package org.motechproject.tamadatasetup.domain;

import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class DataSetupConfiguration {
    private Properties properties;

    @Autowired
    public DataSetupConfiguration(@Qualifier("pillReminderDataSetup") Properties properties) {
        this.properties = properties;
    }

    public int percentageOfPillTaken() {
        return intValue("percentageOfPillTaken");
    }

    private int intValue(String key) {
        return Integer.parseInt(stringValue(key));
    }

    private String stringValue(String key) {
        return properties.getProperty(key);
    }

    public int numberOfDaysToRunFor() {
        return intValue("numberOfDaysToRunFor");
    }

    public Time morningDoseTime() {
        return Time.parseTime(stringValue("morningDoseTime"), ":");
    }

    public Time eveningDoseTime() {
        return Time.parseTime(stringValue("eveningDoseTime"), ":");
    }

    public String phoneNumber() {
        return stringValue("phoneNumber");
    }

    public String pinNumber() {
        return stringValue("pinNumber");
    }
}
