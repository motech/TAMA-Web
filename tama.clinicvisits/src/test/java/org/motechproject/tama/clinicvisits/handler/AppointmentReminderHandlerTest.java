package org.motechproject.tama.clinicvisits.handler;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentReminderHandlerTest {

    @Mock
    OutboxService outboxService;

    @Mock
    AllPatients allPatients;

    MotechEvent event;

    private AppointmentReminderHandler appointmentReminderHandler;

    public AppointmentReminderHandlerTest() {
        initMocks(this);

        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(EventKeys.EXTERNAL_ID_KEY, "patientId");
        event = new MotechEvent(EventKeys.REMINDER_EVENT_SUBJECT, eventParams);

        appointmentReminderHandler = new AppointmentReminderHandler(outboxService, allPatients);
    }

    @Before
    public void setup() {
        reset(outboxService);
    }

    private void whenNoValidOutboxMessageExists(String patientId) {
        when(outboxService.hasPendingOutboxMessages(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);
    }

    private void whenValidOutboxMessageExists(String patientId) {
        when(outboxService.hasPendingOutboxMessages(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
    }

    @Test
    public void shouldCreateOutboxMessageWhenNoValidOutboxMessageExists() {
        String patientId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();

        whenNoValidOutboxMessageExists(patientId);
        when(allPatients.get(patientId)).thenReturn(patient);
        appointmentReminderHandler.handleEvent(event);
        verify(outboxService).addMessage(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotCreateOutboxMessageIfPatientHasNotOptedToReceiveAppointmentReminders() {
        String patientId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(false).build();

        whenNoValidOutboxMessageExists(patientId);
        when(allPatients.get(patientId)).thenReturn(patient);
        appointmentReminderHandler.handleEvent(event);
        verify(outboxService, never()).addMessage(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotCreateOutboxMessageWhenValidOutboxMessageExists() {
        String patientId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();

        whenValidOutboxMessageExists(patientId);
        when(allPatients.get(patientId)).thenReturn(patient);
        appointmentReminderHandler.handleEvent(event);
        verify(outboxService, never()).addMessage(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }
}
