package org.motechproject.tama.healthtips.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class HealthTipsProperties {

    private Properties healthTipProperties;

    @Autowired
    public HealthTipsProperties(@Qualifier("healthtipConstants") Properties healthTipProperties) {
        this.healthTipProperties = healthTipProperties;
    }

    public int getExpiryForPriority1Tips() {
        return Integer.parseInt(healthTipProperties.getProperty("priority.1.expiry"));
    }

    public int getExpiryForPriority2Tips() {
        return Integer.parseInt(healthTipProperties.getProperty("priority.2.expiry"));
    }

    public int getExpiryForPriority3Tips() {
        return Integer.parseInt(healthTipProperties.getProperty("priority.3.expiry"));
    }

    public int getHealthTipPlayCount() {
        return Integer.parseInt(healthTipProperties.getProperty("healthtip.playcount"));
    }
}
