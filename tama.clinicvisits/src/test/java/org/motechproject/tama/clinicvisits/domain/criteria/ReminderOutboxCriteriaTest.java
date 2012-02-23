package org.motechproject.tama.clinicvisits.domain.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ReminderOutboxCriteriaTest {

    @Mock
    OutboxService outboxService;

    ReminderOutboxCriteria reminderOutboxCriteria;

    @Before
    public void setUp() {
        initMocks(this);

        reminderOutboxCriteria = new ReminderOutboxCriteria(outboxService);
    }

    @Test
    public void shouldReturnFalseIfPatientHasOptedNotToReceiveAppointmentReminder() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(false).build();
        Appointment appointment = new Appointment();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessage(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfPatientHasPendingOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessage(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfAppointmentIsAlreadyConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment().firmDate(DateUtil.now());
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessage(patient, appointment));
    }

    @Test
    public void shouldReturnTrue() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessage(patient, appointment));
    }


}
