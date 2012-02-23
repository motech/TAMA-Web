package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
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
    private AllPatients allPatients;
    @Mock
    private AllAppointments allAppointments;
    @Mock
    private AppointmentReminderService appointmentReminderService;

    private Patient patient;
    private Appointment appointment;
    private MotechEvent event;

    private AppointmentReminderHandler appointmentReminderHandler;

    public AppointmentReminderHandlerTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        appointment = new Appointment();
        event = ReminderEventBuilder.startRecording().withPatient(patient).withAppointment(appointment).build();
    }

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderHandler = new AppointmentReminderHandler(allPatients, allAppointments, appointmentReminderService);
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allAppointments.get(appointment.id())).thenReturn(appointment);
    }

    @Test
    public void shouldAddOutboxMessage() {
        appointmentReminderHandler.handleEvent(event);
        verify(appointmentReminderService).addOutboxMessage(patient);
    }


    @Test
    public void shouldRaiseAlert() {
        appointmentReminderHandler.handleEvent(event);
        verify(appointmentReminderService).raiseAlert(patient, appointment);
    }
}
