package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.builder.ReminderEventBuilder;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
import org.motechproject.tama.clinicvisits.service.AppointmentReminderService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentReminderHandlerTest {

    @Mock
    private AppointmentReminderService appointmentReminderService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllAppointments allAppointments;

    protected AppointmentReminderHandler appointmentReminderHandler;

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderHandler = new AppointmentReminderHandler(allPatients, appointmentReminderService, allAppointments);
    }

    @Test
    public void shouldCallAppointmentReminderServiceWhenAnEventIsRaised() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
        Appointment appointment = new Appointment();
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allAppointments.get(appointment.id())).thenReturn(appointment);

        appointmentReminderHandler.handleEvent(ReminderEventBuilder.startRecording().withPatient(patient).withAppointment(appointment).build());

        verify(appointmentReminderService).raiseOutboxMessage(patient);
        verify(appointmentReminderService).raiseReminderAlert(patient, appointment);
    }
}
