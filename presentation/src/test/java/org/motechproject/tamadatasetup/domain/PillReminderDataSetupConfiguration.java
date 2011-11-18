package org.motechproject.tamadatasetup.domain;

import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PillReminderDataSetupConfiguration extends DataSetupConfiguration {
    @Autowired
    public PillReminderDataSetupConfiguration(@Qualifier("pillReminderDataSetup") Properties properties) {
        super(properties);
    }

    public int percentageOfPillTaken() {
        return intValue("percentageOfPillTaken");
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
