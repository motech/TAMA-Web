package org.motechproject.tama.clinicvisits.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.service.AppointmentService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllAppointmentsTest {

    @Mock
    private AppointmentService appointmentService;
    protected AllAppointments allAppointments;

    @Before
    public void setup() {
        initMocks(this);
        allAppointments = new AllAppointments(appointmentService);
    }

    @Test
    public void shouldGetAppointment() {
        Appointment appointment = new Appointment();
        when(appointmentService.getAppointment(appointment.id())).thenReturn(appointment);
        assertEquals(appointment, allAppointments.get(appointment.id()));
    }
}
