package org.motechproject.tama.clinicvisits.mapper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AppointmentCalendarRequestBuilderTest {

    private AppointmentCalendarRequestBuilder builder;

    @Before
    public void setUp() {
        Properties appointmentsProperties = new Properties();
        appointmentsProperties.put(ReminderConfigurationMapper.REMIND_FROM, "10");
        appointmentsProperties.put(AppointmentCalendarRequestBuilder.APPOINTMENT_SCHEDULE, "4,12,24");
        VisitRequestBuilder visitRequestBuilder = new VisitRequestBuilder(new ReminderConfigurationMapper(appointmentsProperties));
        builder = new AppointmentCalendarRequestBuilder(visitRequestBuilder, appointmentsProperties);
    }

    @Test
    public void shouldCreateAppointmentCalendarRequest() {
        String patientDocId = "patientDocId";
        AppointmentCalendarRequest calendarRequest = builder.calendarForPatient(patientDocId);
        assertEquals(patientDocId, calendarRequest.getExternalId());
        assertEquals(4, calendarRequest.getVisitRequests().size());
        assertNotNull(calendarRequest.getVisitRequests().get("baseline"));
        assertNotNull(calendarRequest.getVisitRequests().get("week4"));
        assertNotNull(calendarRequest.getVisitRequests().get("week12"));
        assertNotNull(calendarRequest.getVisitRequests().get("week24"));
    }
}
