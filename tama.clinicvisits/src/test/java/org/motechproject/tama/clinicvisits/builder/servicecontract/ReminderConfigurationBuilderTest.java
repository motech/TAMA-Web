package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class ReminderConfigurationBuilderTest {

    private ReminderConfigurationBuilder builder;

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FROM, "10");
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FOR_VISIT_FROM, "5");
        builder = new ReminderConfigurationBuilder(appointmentsProperties);
    }

    @Test
    public void shouldMapAppointmentReminderConfiguration() {
        ReminderConfiguration reminderConfiguration = builder.newAppointmentReminder();
        assertEquals(1, reminderConfiguration.getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, reminderConfiguration.getIntervalUnit());
        assertEquals(10 + 1, reminderConfiguration.getRepeatCount());
        assertEquals(10, reminderConfiguration.getRemindFrom());
    }

    @Test
    public void shouldMapVisitReminderConfiguration() {
        ReminderConfiguration reminderConfiguration = builder.newVisitReminder();
        assertEquals(1, reminderConfiguration.getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, reminderConfiguration.getIntervalUnit());
        assertEquals(5 + 1, reminderConfiguration.getRepeatCount());
        assertEquals(5, reminderConfiguration.getRemindFrom());
    }
}
