package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.ConfirmAppointmentRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ConfirmAppointmentRequestBuilderTest {

    @Mock
    private ReminderConfigurationBuilder reminderConfigurationBuilder;
    private ConfirmAppointmentRequestBuilder confirmAppointmentRequestBuilder;

    @Before
    public void setUp() {
        initMocks(this);
        confirmAppointmentRequestBuilder = new ConfirmAppointmentRequestBuilder(reminderConfigurationBuilder);
    }

    @Test
    public void createConfirmAppointmentRequest() {
        DateTime now = DateUtil.now();
        ReminderConfiguration reminder = new ReminderConfiguration();

        when(reminderConfigurationBuilder.newVisitReminder()).thenReturn(reminder);
        ConfirmAppointmentRequest confirmAppointmentRequest = confirmAppointmentRequestBuilder.confirmAppointmentRequest("patientDocId", "visitId", now);

        assertEquals("patientDocId", confirmAppointmentRequest.getExternalId());
        assertEquals("visitId", confirmAppointmentRequest.getVisitName());
        assertEquals(reminder, confirmAppointmentRequest.getVisitReminderConfiguration());
        assertEquals(now, confirmAppointmentRequest.getAppointmentConfirmDate());
    }
}