package org.motechproject.tama.clinicvisits.mapper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class ReminderConfigurationMapperTest {

    private ReminderConfigurationMapper mapper;

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationMapper.REMIND_FROM, "10");
        mapper = new ReminderConfigurationMapper(appointmentsProperties);
    }

    @Test
    public void shouldMapReminderConfiguration() {
        ReminderConfiguration reminderConfiguration = mapper.map();
        assertEquals(1, reminderConfiguration.getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, reminderConfiguration.getIntervalUnit());
        assertEquals(10, reminderConfiguration.getRepeatCount());
        assertEquals(10, reminderConfiguration.getRemindFrom());
    }
}
