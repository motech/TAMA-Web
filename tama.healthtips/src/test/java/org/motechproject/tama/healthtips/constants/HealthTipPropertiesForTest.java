package org.motechproject.tama.healthtips.constants;

import org.motechproject.tama.healthtips.domain.HealthTipsProperties;

import java.util.Properties;

public class HealthTipPropertiesForTest extends HealthTipsProperties {

    public HealthTipPropertiesForTest(){
        super(new Properties());
    }

    @Override
    public int getExpiryForPriority1Tips() {
        return 7;
    }

    @Override
    public int getExpiryForPriority2Tips() {
        return 14;
    }

    @Override
    public int getExpiryForPriority3Tips() {
        return 21;
    }

    @Override
    public int getHealthTipPlayCount() {
        return 2;
    }
}
