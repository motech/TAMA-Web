package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationClinicVisitsContext.xml")
public class TAMAReminderConfigurationIT {

    @Autowired
    private TAMAReminderConfiguration tamaReminderConfiguration;

    @Test
    public void shouldReturnTheStartDateOfAppointmentRemindersForClinicVisit() {
        DateTime now = DateUtil.now();
        ClinicVisit clinicVisit = mock(ClinicVisit.class);

        when(clinicVisit.getAppointmentDueDate()).thenReturn(now);
        assertEquals(now.minusDays(10), tamaReminderConfiguration.reminderStartDate(clinicVisit));
    }

    @Test
    public void shouldReturnTheNumberOfTimesToPushAppointmentReminderOutboxMessage() {
        assertEquals(2, tamaReminderConfiguration.getPushedAppointmentReminderVoiceMessageCount());
    }

    @Test
    public void shouldReturnTheNumberOfTimesToPushVisitReminderOutboxMessage() {
        assertEquals(2, tamaReminderConfiguration.getPushedVisitReminderVoiceMessageCount());
    }
}
