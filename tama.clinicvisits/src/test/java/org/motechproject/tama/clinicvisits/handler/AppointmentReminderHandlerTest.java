package org.motechproject.tama.clinicvisits.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.clinicvisits.builder.ReminderEventBuilder;
import org.motechproject.tama.clinicvisits.domain.CreateAppointmentReminderCriteria;
import org.motechproject.tama.clinicvisits.domain.RaiseAppointmentConfirmationCriteria;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentReminderHandlerTest {

    @Mock
    private OutboxService outboxService;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllAppointments allAppointments;
    @Mock
    private CreateAppointmentReminderCriteria appointmentReminderCriteria;
    @Mock
    private RaiseAppointmentConfirmationCriteria appointmentConfirmationCriteria;

    private Patient patient;
    private Appointment appointment;
    private MotechEvent event;

    private AppointmentReminderHandler appointmentReminderHandler;

    public AppointmentReminderHandlerTest() {
        patient = PatientBuilder
                .startRecording()
                .withDefaults()
                .withId("patientDocumentId")
                .build();

        appointment = new Appointment();

        event = ReminderEventBuilder.startRecording().withPatient(patient).withAppointment(appointment).build();
    }

    @Before
    public void setup() {
        initMocks(this);

        appointmentReminderHandler = new AppointmentReminderHandler(allPatients,
                patientAlertService,
                allAppointments,
                outboxService,
                appointmentReminderCriteria,
                appointmentConfirmationCriteria);

        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allAppointments.get(appointment.id())).thenReturn(appointment);
    }

    @Test
    public void shouldOutboxMessageWhenCreateAppointmentReminderCriteriaIsTrue() {
        when(appointmentReminderCriteria.shouldRaiseReminder(patient)).thenReturn(true);

        appointmentReminderHandler.handleEvent(event);

        verify(outboxService).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotOutboxMessageWhenCreateAppointmentReminderCriteriaIsFalse() {
        when(appointmentReminderCriteria.shouldRaiseReminder(patient)).thenReturn(false);

        appointmentReminderHandler.handleEvent(event);

        verifyZeroInteractions(outboxService);
    }

    @Test
    public void shouldRaiseAlertIfRaiseAppointmentConfirmationCriteriaIsTrue() {
        when(appointmentConfirmationCriteria.shouldRaiseAlert(appointment)).thenReturn(true);

        appointmentReminderHandler.handleEvent(event);

        verify(patientAlertService).createAlert(patient.getId(),
                TAMAConstants.NO_ALERT_PRIORITY,
                TAMAConstants.APPOINTMENT_REMINDER,
                "",
                PatientAlertType.AppointmentReminder,
                null);
    }

    @Test
    public void shouldNotRaiseAlertIfRaiseAppointmentConfirmationCriteriaIsFalse() {
        when(appointmentConfirmationCriteria.shouldRaiseAlert(appointment)).thenReturn(false);

        appointmentReminderHandler.handleEvent(event);

        verifyZeroInteractions(patientAlertService);
    }
}
