package org.motechproject.tama.clinicvisits.mapper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class ReminderConfigurationBuilderTest {

    private ReminderConfigurationBuilder builder;

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FROM, "10");
        builder = new ReminderConfigurationBuilder(appointmentsProperties);
    }

    @Test
    public void shouldMapReminderConfiguration() {
        ReminderConfiguration reminderConfiguration = builder.newDefault();
        assertEquals(1, reminderConfiguration.getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, reminderConfiguration.getIntervalUnit());
        assertEquals(10, reminderConfiguration.getRepeatCount());
        assertEquals(10, reminderConfiguration.getRemindFrom());
    }
}
