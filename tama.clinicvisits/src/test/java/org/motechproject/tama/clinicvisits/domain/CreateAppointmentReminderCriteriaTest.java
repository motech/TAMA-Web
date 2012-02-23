package org.motechproject.tama.clinicvisits.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreateAppointmentReminderCriteriaTest {

    @Mock
    OutboxService outboxService;

    CreateAppointmentReminderCriteria createAppointmentReminderCriteria;

    @Before
    public void setUp() {
        initMocks(this);
        createAppointmentReminderCriteria = new CreateAppointmentReminderCriteria(outboxService);
    }

    @Test
    public void shouldNotCreateOutboxMessageIfPatientHasOptedNotToReceiveAppointmentReminder() {
        Patient patient = PatientBuilder
                .startRecording()
                .withDefaults()
                .withAppointmentReminderPreference(false)
                .build();

        assertFalse(createAppointmentReminderCriteria.shouldRaiseReminder(patient));
    }

    @Test
    public void shouldNotCreateOutboxMessageIfPatientHasPendingOutboxMessage() {
        Patient patient = PatientBuilder
                .startRecording()
                .withDefaults()
                .withAppointmentReminderPreference(true)
                .build();

        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);

        assertFalse(createAppointmentReminderCriteria.shouldRaiseReminder(patient));
    }

    @Test
    public void shouldCreateOutboxMessage() {
        Patient patient = PatientBuilder
                .startRecording()
                .withDefaults()
                .withAppointmentReminderPreference(true)
                .build();

        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);

        assertTrue(createAppointmentReminderCriteria.shouldRaiseReminder(patient));
    }
}
