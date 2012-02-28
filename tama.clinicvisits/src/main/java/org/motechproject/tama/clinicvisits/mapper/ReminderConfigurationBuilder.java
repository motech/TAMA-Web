package org.motechproject.tama.clinicvisits.mapper;

import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ReminderConfigurationBuilder {

    public static final String REMIND_FROM = "remindFrom";

    private Properties appointmentsProperties;

    @Autowired
    public ReminderConfigurationBuilder(@Qualifier("appointments") Properties appointmentsProperties) {
        this.appointmentsProperties = appointmentsProperties;
    }

    public ReminderConfiguration newDefault() {
        int remindFrom = Integer.parseInt(appointmentsProperties.getProperty(REMIND_FROM));
        return new ReminderConfiguration().setRemindFrom(remindFrom).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.DAYS).setRepeatCount(remindFrom);
    }
}
