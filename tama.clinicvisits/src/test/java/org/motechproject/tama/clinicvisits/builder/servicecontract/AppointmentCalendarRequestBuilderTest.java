package org.motechproject.tama.clinicvisits.builder.servicecontract;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.service.contract.AppointmentCalendarRequest;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class AppointmentCalendarRequestBuilderTest {

    private AppointmentCalendarRequestBuilder builder;

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationBuilder.REMIND_FROM, "10");
        appointmentsProperties.put(AppointmentCalendarRequestBuilder.APPOINTMENT_SCHEDULE, "4,12,24");
        CreateVisitRequestBuilder createVisitRequestBuilder = new CreateVisitRequestBuilder(new ReminderConfigurationBuilder(appointmentsProperties));
        builder = new AppointmentCalendarRequestBuilder(createVisitRequestBuilder, appointmentsProperties);
    }

    @Test
    public void shouldCreateAppointmentCalendarRequest() {
        String patientDocId = "patientDocId";
        AppointmentCalendarRequest calendarRequest = builder.calendarForPatient(patientDocId);
        assertEquals(patientDocId, calendarRequest.getExternalId());
        assertEquals(4, calendarRequest.getCreateVisitRequests().size());
        assertEquals("baseline", calendarRequest.getCreateVisitRequests().get(0).getVisitName());
        assertEquals("week4", calendarRequest.getCreateVisitRequests().get(1).getVisitName());
        assertEquals("week12", calendarRequest.getCreateVisitRequests().get(2).getVisitName());
        assertEquals("week24", calendarRequest.getCreateVisitRequests().get(3).getVisitName());
    }
}
